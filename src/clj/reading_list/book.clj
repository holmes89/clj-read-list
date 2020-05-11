(ns reading-list.book
  (:require [schema.core :as s]
            [ring.util.http-response :refer [ok not-found created]]
            [compojure.api.sweet :refer :all]
            [reading-list.repository :as repo]
            [clojure.tools.logging :as log]))

(defn uuid [] (.toString (java.util.UUID/randomUUID)))

(s/defschema BookSchema
  {:id s/Uuid
   :title s/Str
   :author s/Str
   :isbn s/Str
   (s/optional-key :notes) s/Str
   })

(s/defschema BookRequest
  {:title s/Str
   :author s/Str
   :isbn s/Str
   (s/optional-key :notes) s/Str
   })

(def example
  {:id (uuid)
   :title "title"
   :author "author"
   :isbn "1234567890"
   :notes "it was super good"
   })

(defn response [book]
  (if book
    (ok book)
    (not-found)))

(defn get-book-handler [id]
  (response (repo/get-book (.toString id))))

(defn get-all-books-handler []
  (response (list example)))

(defn add-book [req]
  (->    
    (assoc req :id (uuid))
    (repo/add-book!)
    (response)))

(defroutes book-routes
  (context "/books" []
           (GET "/" []
             (get-all-books-handler))
           (GET "/:id" []
             :path-params [id :- s/Uuid]
             (get-book-handler id))
           (POST "/" request
             :responses {created BookSchema}
             :body [req BookRequest]
             (add-book req))))
