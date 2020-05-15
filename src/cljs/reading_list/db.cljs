(ns reading-list.db
  (:require [schema.core :as s]))

(s/defschema BookSchema
  {:id s/Uuid
   :title s/Str
   (s/optional-key :author) s/Str
   :isbn s/Str
   :thumbnail s/Str
   (s/optional-key :notes) s/Str
   })

(s/defschema BookRequest
  {(s/optional-key :title) s/Str
   (s/optional-key :author) s/Str
   :isbn s/Str
   (s/optional-key :notes) s/Str
   })


(def default-db
  {:name "re-frame"
   :loading? false
   :book-list []
   })
