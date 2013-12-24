(ns adrastea.simulated.start
  (:require [io.pedestal.app.render.push.handlers.automatic :as d]
            [adrastea.start :as start]
            [adrastea.rendering :as rendering]
            [goog.Uri]
            [io.pedestal.app.protocols :as p]
            [adrastea.simulated.services :as services]
            ;; tools to work.
            [io.pedestal.app-tools.tooling :as tooling]))

(defn param [name]
  (let [uri (goog.Uri. (.toString  (.-location js/document)))]
    (.getParameterValue uri name)))

(defn ^:export main []
  (let [app (start/create-app d/data-renderer-config)
        services (services/->MockServices (:app app))]
    (p/start services)
    app))
