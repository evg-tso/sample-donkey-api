(ns sample-donkey-api.http.client
  (:require [integrant.core :as ig]
            [com.appsflyer.donkey.client :as donkey-client]
            [com.appsflyer.donkey.core :as donkey-core])
  (:import (com.appsflyer.donkey.client DonkeyClient)))

(defn- create-donkey-client ^DonkeyClient [donkey]
  (donkey-core/create-client
    donkey
    {:idle-timeout-seconds    30
     :connect-timeout-seconds 20
     :compression             true}))

(defmethod ig/init-key :http/client [_ {:keys [donkey]}]
  (create-donkey-client donkey))

(defmethod ig/halt-key! :http/client [_ client]
  (donkey-client/stop client))
