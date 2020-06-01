(ns reading-list.repository
  (:require [taoensso.carmine :as car :refer (wcar)]
            [environ.core :refer [env]]))

(def server-conn {:pool {} :spec {:uri (env :redis-url "redis://localhost/")}})
(defmacro wcar* [& body] `(car/wcar server-conn ~@body))

(defn add-to-list [list id]
  (wcar* (car/lpush list id)))

(defn get-list [list]
  (wcar* (car/lrange list 0 -1)))

(defn add-book! [book]
  (let [id (get book :id)]
    (wcar*
      (car/set id book))
    (add-to-list "all" id)) book)

(defn update-book [id book]
  (wcar*
    (car/set id book)) book)

(defn get-book [id]
  (wcar* (car/get id)))
