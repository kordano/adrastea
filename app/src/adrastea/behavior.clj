(ns ^:shared adrastea.behavior
    (:require [clojure.string :as string]
              [io.pedestal.app.messages :as msg]
              [io.pedestal.app.util.platform :as platform]
              [io.pedestal.app :as app]))


(set! clojure.core/*print-fn* (fn [& s] (.log js/console (apply str s))))


(defn bookmark-transform [old-value {title :title tag :tag url :url}]
  (let [new-entry {:title title :url url :tag tag :time (platform/date) :uuid (UUID. url) :user nil}]
    (if (nil? old-value)
      [new-entry]
      (conj old-value new-entry))))


(defn apply-bookmark-user [old-value new-value]
  ;TODO: better bookmark-user handling
  (if (nil? old-value)
    nil
    (if (nil? (get-in old-value [(dec (count old-value)) :user]))
      (assoc-in old-value [(dec (count old-value)) :user] (new-value [:user]))
      old-value)))


(defn set-value-transform [_ message]
  (:value message))


(defn init-main [_]
  [{:bookmarks
    {:transforms
     {:add-bookmark [{msg/topic [:bookmarks]
                      (msg/param :title) {}
                      (msg/param :url) {}
                      (msg/param :tag) {}}]}}
    :user
    {:transforms
     {:set-user [{msg/topic [:user]
                  (msg/param :value) {}}]}}}])


(def bookmark-app
  {:version 2
   :debug true
   :transform [[:add-bookmark [:bookmarks] bookmark-transform]
               [:set-user [:user] set-value-transform]
               [:swap [:**] set-value-transform]]
   :derive #{[#{[:user] [:bookmarks]} [:bookmarks] apply-bookmark-user :map]}
   :emit [{:init init-main}
          [#{[:bookmarks]
             [:user]
             [:import :*]}
           (app/default-emitter [])]]})
