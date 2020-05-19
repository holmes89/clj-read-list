(ns reading-list.events
  (:require
   [ajax.core :as ajax]        
   [day8.re-frame.http-fx]
   [re-frame.core :as re-frame]
   [reading-list.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
   db/default-db))

(re-frame/reg-event-db
    ::set-active-panel
  (fn-traced [db [_ active-panel]]
             (assoc db :active-panel active-panel)))

(re-frame/reg-event-db                   
    ::process-response             
  (fn
    [db [_ response]]
    (-> db
        (assoc :loading? false)
        (assoc :book-list (js->clj response)))))


(re-frame/reg-event-db                   
    ::bad-response             
  (fn
    [message]
    (js/console.log  message)))

(defn read?
  [book]
  (:read book))

(def read-filter
  (filter read?))

(defn unread?
  [book]
  (not (:read book)))

(def unread-filter
  (filter unread?))

(defn liked?
  [book]
  (:liked book))

(def liked-filter
  (filter liked?))

(re-frame/reg-event-db
    ::toggle-read-filter
  (fn
    [db]
    (as-> (update-in db [:read-filter-enabled?] not) d
      (if (contains? (:filters d) read-filter) 
        (update-in d [:filters] disj read-filter)
        (update-in d [:filters] conj read-filter)))))

(re-frame/reg-event-db
    ::toggle-unread-filter
  (fn
    [db]
    (as-> (update-in db [:unread-filter-enabled?] not) d
      (if (contains? (:filters d) unread-filter)
        (update-in d [:filters] disj unread-filter)
        (update-in d [:filters] conj unread-filter)))))

(re-frame/reg-event-db
    ::toggle-liked-filter
  (fn
    [db]
    (as-> (update-in db [:liked-filter-enabled?] not) d
      (if (contains? (:filters d) liked-filter)
        (update-in d [:filters] disj liked-filter)
        (update-in d [:filters] conj liked-filter)))))

(re-frame/reg-event-fx
    ::add-book
  (fn
    [{db :db} [_ body]]
    {:http-xhrio {:method          :post
                  :uri              "/books/"
                  :params body
                  :format          (ajax/json-request-format {:keywords? true})
                  :response-format (ajax/json-response-format {:keywords? true}) 
                  :on-success      [::get-books]
                  :on-failure      [::bad-response]
                  }
     :db  (assoc db :loading? true)}))

(re-frame/reg-event-fx
    ::update-book
  (fn
    [{db :db} [_ body]]
    {:http-xhrio {:method          :put
                  :uri              (str "/books/" (get body :id))
                  :params body
                  :format          (ajax/json-request-format {:keywords? true})
                  :response-format (ajax/json-response-format {:keywords? true}) 
                  :on-success      [::get-books]
                  :on-failure      [::bad-response]
                  }
     :db  (assoc db :loading? true)}))

(re-frame/reg-event-fx
    ::get-books
  (fn
    [{db :db} _]
    {:http-xhrio {:method          :get
                  :uri             "/books/"
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true}) 
                  :on-success      [::process-response]
                  :on-failure      [::bad-response]
                  }
     :db  (assoc db :loading? true)}))
