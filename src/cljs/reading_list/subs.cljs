(ns reading-list.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
    ::active-panel
  (fn [db _]
    (:active-panel db)))


(re-frame/reg-sub
    ::books
  (fn [db _]
    (->> (:book-list db)
         (sequence (apply comp (:filters db))))))

(re-frame/reg-sub
    ::read-filter-enabled
  (fn [db _]
    (:read-filter-enabled? db)))

(re-frame/reg-sub
    ::liked-filter-enabled
  (fn [db _]
    (:liked-filter-enabled? db)))

(re-frame/reg-sub
    ::unread-filter-enabled
  (fn [db _]
    (:unread-filter-enabled? db)))
