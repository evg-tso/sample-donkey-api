(ns sample-donkey-api.http.external.server
  (:require [sample-donkey-api.http.server-common :as server-common]
            [integrant.core :as ig]
            [com.appsflyer.donkey.server :as donkey-server]
            [com.brunobonacci.mulog :as logger]))

(defn- start-server [donkey router port]
  (let [server-config {:port                 port
                       :keep-alive           true
                       :idle-timeout-seconds 30
                       :compression          true
                       :decompression        true
                       :routes               router}]
    (server-common/create-server donkey server-config)))

(defmethod ig/init-key :external/server [_ {:keys [donkey router config]}]
  (start-server donkey router (-> config :http-server :external :port)))

(defmethod ig/halt-key! :external/server [_ server]
  (logger/log ::server-stopped)
  (donkey-server/stop-sync server))
