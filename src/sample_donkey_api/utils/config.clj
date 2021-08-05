(ns sample-donkey-api.utils.config
  (:require [integrant.core :as ig]
            [cprop.core :as cprop]
            [cprop.source]
            cprop.tools))

(defmethod ig/init-key :utils/config [_ _]
  (cprop/load-config :merge [(cprop.source/from-env)
                             (cprop.source/from-system-props)]))
