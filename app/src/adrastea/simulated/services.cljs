(ns adrastea.simulated.services
  (:require [io.pedestal.app.protocols :as p]
            [io.pedestal.app.messages :as msg]
            [io.pedestal.app.util.platform :as platform]))

;; Implement services to simulate talking to back-end services

(set! clojure.core/*print-fn* (fn [& s] (.log js/console (apply str s))))


(def bookmarks (atom {"adam" [] "eva" []}))


(defn random-string [length]
  (let [ascii-codes (concat (range 48 58) (range 66 91) (range 97 123))]
    (apply str (repeatedly length #(char (rand-nth ascii-codes))))))


(defn append-bookmark [key t input-queue]
  (let [new-url (random-string 10)
        new-tag (random-string 3)]
    (p/put-message input-queue {msg/type :swap
                                msg/topic [:import key]
                                :value (get
                                        (swap! bookmarks
                                               update-in [key]
                                               #(conj % {:url new-url
                                                         :tag new-tag
                                                         :time (platform/date)
                                                         :uuid (UUID. new-url)}))
                                        key)})
    (platform/create-timeout t #(append-bookmark key t input-queue))))


(defn receive-messages [input-queue]
  (append-bookmark "adam" 60000 input-queue)
  (append-bookmark "eva" 30000 input-queue))


(defrecord MockServices [app]
  p/Activity
  (start [this]
    (receive-messages (:input app)))
  (stop [this]))
