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
            [sample-donkey-api.http.external.controller]
            [clojure.java.io :as io]
            [com.walmartlabs.dyn-edn :as dyn-edn]
            [clojure.edn :as edn]
            [sample-donkey-api.http.client]
            [sample-donkey-api.infrastructure.repository.ip-resolver]))

(defn start-application []
  (let [states-map (->> "config.edn"
                        io/resource
                        slurp
                        (edn/read-string {:eof     nil
                                          :readers (merge (dyn-edn/env-readers)
                                                          {'ig/ref    ig/ref
                                                           'ig/refset ig/refset})}))]
    (ig/init states-map)))

(defn stop-application [system-map]
  (ig/halt! system-map))
