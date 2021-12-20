(ns sample-donkey-api.http.http-factory
  (:require
    [com.appsflyer.donkey.core :as donkey-core]
    [integrant.core :as ig]))

(defn- start-donkey-factory []
  (donkey-core/create-donkey))

(defmethod ig/init-key :http-factory/donkey [_ _]
  (start-donkey-factory))

(defmethod ig/halt-key! :http-factory/donkey [_ donkey]
  (donkey-core/destroy donkey))
