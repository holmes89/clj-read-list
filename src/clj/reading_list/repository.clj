(ns reading-list.repository
  (:require [taoensso.carmine :as car :refer (wcar)]))

(def server-conn {:pool {} :spec {:uri "redis://localhost/"}})
(defmacro wcar* [& body] `(car/wcar server-conn ~@body))

(defn add-book! [book]
  (let [results (wcar* (car/set
                         (get book :id)
                         book))]
    (if (= results "OK")
      book
      (throw (Exception. "unable to write to repository" results)))
    ))
(defn get-book [id]
  (wcar* (car/get id)))
