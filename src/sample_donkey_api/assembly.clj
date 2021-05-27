(ns sample-donkey-api.assembly
  (:require [integrant.core :as ig]
            [sample-donkey-api.application.model.stock-order]
            [sample-donkey-api.application.service.validation]
            [sample-donkey-api.http.internal.server]
            [sample-donkey-api.http.internal.routes]
            [sample-donkey-api.http.internal.controller]
            [sample-donkey-api.http.http-factory]
            [sample-donkey-api.http.external.server]
            [sample-donkey-api.http.external.routes]
            [sample-donkey-api.http.external.controller]))

(defn start-application []
  (let [states-map (ig/read-string (slurp "resources/config.edn"))]
    (ig/init states-map)))

(defn stop-application [system-map]
  (ig/halt! system-map))
