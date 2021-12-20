(ns sample-donkey-api.assembly
  (:require [integrant.core :as ig]
            [sample-donkey-api.application.model.stock-order]
            [sample-donkey-api.http.internal.server]
            [sample-donkey-api.http.internal.routes]
            [sample-donkey-api.http.internal.controller]
            [sample-donkey-api.http.http-factory]
            [sample-donkey-api.http.external.server]
            [sample-donkey-api.http.external.routes]
            [sample-donkey-api.http.external.controller]
            [clojure.java.io :as io]
            [sample-donkey-api.utils.config]
            [sample-donkey-api.http.client]
            [sample-donkey-api.infrastructure.repository.ip-resolver]
            [sample-donkey-api.infrastructure.repository.kafka-producer]
            [sample-donkey-api.application.service.event-processor]))

(defn start-application []
  (let [states-map (->> "states.edn"
                        io/resource
                        slurp
                        ig/read-string)]
    (ig/init states-map)))

(defn stop-application [system-map]
  (ig/halt! system-map))
