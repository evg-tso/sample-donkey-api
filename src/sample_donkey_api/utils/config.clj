(ns sample-donkey-api.utils.config
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [com.walmartlabs.dyn-edn :as dyn-edn]
    [integrant.core :as ig]))

(defmethod ig/init-key :utils/config [_ _]
  (->> "config.edn"
       io/resource
       slurp
       (edn/read-string {:eof     nil
                         :readers (dyn-edn/env-readers)})))
