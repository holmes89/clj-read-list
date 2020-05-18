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

(defn read?
  [book]
  (:read book))

(defn unread?
  [book]
  (not (:read book)))

(defn liked?
  [book]
  (:liked book))

(re-frame/reg-sub
    ::books
  (fn [db _]
    (:book-list db)))
