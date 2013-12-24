(ns adrastea.start
  (:require [io.pedestal.app.protocols :as p]
            [io.pedestal.app :as app]
            [io.pedestal.app.render.push :as push-render]
            [io.pedestal.app.render :as render]
            [io.pedestal.app.messages :as msg]
            [adrastea.behavior :as behavior]
            [adrastea.rendering :as rendering]))


(defn create-app [render-config]
  (let [app (app/build behavior/bookmark-app)
        render-fn (push-render/renderer "content" render-config render/log-fn)
        app-model (render/consume-app-model app render-fn)]
    (app/begin app)
    (p/put-message (:input app) {msg/type :set-user msg/topic [:user] :value "konny"})
    {:app app :app-model app-model}))

(defn ^:export main []
  (create-app (rendering/render-config)))
