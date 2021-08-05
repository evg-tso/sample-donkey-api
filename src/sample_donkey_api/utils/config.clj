(ns sample-donkey-api.utils.config
  (:require [integrant.core :as ig]
            [com.walmartlabs.dyn-edn :as dyn-edn]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defmethod ig/init-key :utils/config [_ _]
  (->> "config.edn"
       io/resource
       slurp
       (edn/read-string {:eof     nil
                         :readers (dyn-edn/env-readers)})))
