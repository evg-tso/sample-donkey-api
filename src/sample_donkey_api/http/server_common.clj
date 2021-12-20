(ns sample-donkey-api.http.server-common
  (:require
    [com.appsflyer.donkey.core :as donkey]
    [com.appsflyer.donkey.server :as donkey-server]
    [com.brunobonacci.mulog :as logger]))

(defn create-server [donkey server-config]
  (let [server (donkey/create-server donkey server-config)]
    (try
      (donkey-server/start-sync server)
      (catch Throwable e
        (logger/log ::error-starting-server :exception e :port (:port server-config))))
    server))
