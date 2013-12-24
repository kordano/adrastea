(ns ^:shared adrastea.behavior
    (:require [clojure.string :as string]
              [io.pedestal.app.messages :as msg]
              [io.pedestal.app.util.platform :as platform]
              [io.pedestal.app :as app]))


(set! clojure.core/*print-fn* (fn [& s] (.log js/console (apply str s))))


(defn bookmark-transform [old-value {tag :tag url :url}]
  (let [new-entry {:url url :tag tag :time (platform/date) :uuid (UUID. url)}]
    (if (nil? old-value)
       new-entry
      (if (vector? old-value)
        (conj old-value new-entry)
        (vector old-value new-entry)))))


(defn set-value-transform [_ message]
  (:value message))


(defn init-main [_]
  [{:bookmarks
    {:transforms
      {:add-bookmark [{msg/topic [:bookmarks]
                       (msg/param :url) {}
                       (msg/param :tag) {}}]}
     :user
     {:transforms
      {:set-user [{msg/topic [:user]
                   (msg/param :value) {}}]}}}}])


(def bookmark-app
  {:version 2
   :debug true
   :transform [[:add-bookmark [:bookmarks] bookmark-transform]
               [:set-user [:user] set-value-transform]
               [:swap [:**] set-value-transform]]
   :emit [{:init init-main}
          [#{[:bookmarks]
             [:user]
             [:import :*]}
           (app/default-emitter [])]]})
