(ns reading-list.book
  (:require [schema.core :as s]
            [ring.util.http-response :refer [ok not-found created]]
            [compojure.api.sweet :refer :all]
            [reading-list.repository :as repo]
            [clojure.tools.logging :as log]
            [environ.core :refer [env]]
            [cheshire.core :as ch]))


(def book-key
  (let [k (env :google-key "")]
    (if (not (clojure.string/blank? k))
      (str "key=" k "&")
      "")))

(defn uuid [] (.toString (java.util.UUID/randomUUID)))

(s/defschema BookSchema
  {:id s/Uuid
   :title s/Str
   (s/optional-key :author) s/Str
   :isbn s/Str
   :thumbnail s/Str
   (s/optional-key :read) s/Bool
   (s/optional-key :liked) s/Bool
   (s/optional-key :notes) s/Str
   })

(s/defschema BookRequest
  {(s/optional-key :title) s/Str
   (s/optional-key :author) s/Str
   :isbn s/Str
   (s/optional-key :notes) s/Str
   })


(defn response [book]
  (if book
    (ok book)
    (not-found)))

(defn get-book-handler [id]
  (response (repo/get-book (.toString id))))

(defn get-all-books-handler []
  (->> (repo/get-list "all")
       (map repo/get-book)
       (response)))

(defn book-from-google-api [isbn]
  (-> (clojure.string/trim isbn)
      (slurp (str "https://www.googleapis.com/books/v1/volumes?" book-key "q=isbn%3D" isbn))
      ch/parse-string
      (get "items")
      first
      (get "volumeInfo")))

(defn lookup-book [isbn]
  (let [b (book-from-google-api isbn)]
    {
     :title (get b "title")
     :author (first (get b "authors"))
     :thumbnail (get-in b ["imageLinks" "thumbnail"])
     }))

(defn add-book [req]
  (->>
    (assoc req :id (uuid))
    (merge (lookup-book (get req :isbn)))
    (repo/add-book!)
    (response)))

(defn update-book [id req]
  (->>
    (repo/update-book (.toString id) req)
    (response)))

(defroutes book-routes
  (context "/books" []
           (GET "/" []
             (get-all-books-handler))
           (GET "/:id" []
             :path-params [id :- s/Uuid]
             (get-book-handler id))
           (PUT "/:id" request
                :responses {created BookSchema}
                :body [req BookSchema]
                :path-params [id :- s/Uuid]
                (update-book id req))
           (POST "/" request
             :responses {created BookSchema}
             :body [req BookRequest]
             (add-book req))))
