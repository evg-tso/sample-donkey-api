(ns sample-donkey-api.http.internal.server
  (:require [sample-donkey-api.http.server-common :as server-common]
            [integrant.core :as ig]
            [com.appsflyer.donkey.server :as donkey-server]
            [com.brunobonacci.mulog :as logger]))

(defn- start-server [donkey router config]
  (let [port          (:port config)
        server-config {:port                 port
                       :idle-timeout-seconds 5
                       :routes               router}]
    (server-common/create-server donkey server-config)))

(defmethod ig/init-key :internal/server [_ {:keys [donkey router] :as config}]
  (start-server donkey router config))

(defmethod ig/halt-key! :internal/server [_ server]
  (logger/log ::server-stopped)
  (donkey-server/stop-sync server))
