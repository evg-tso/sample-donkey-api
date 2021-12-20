(ns sample-donkey-api.integration.http-factory-setup
  (:require
    [com.appsflyer.donkey.client :as donkey-client]
    [com.appsflyer.donkey.core :as donkey-core])
  (:import
    (com.appsflyer.donkey.client
      DonkeyClient)))

(def donkey-client (atom nil))

(defn- start-donkey-factory []
  (let [factory-config {:event-loops 1}]
    (donkey-core/create-donkey factory-config)))

(defn- create-donkey-client ^DonkeyClient [donkey]
  (donkey-core/create-client
    donkey
    {:idle-timeout-seconds    30
     :connect-timeout-seconds 20
     :compression             true}))

(defn with-donkey-client [test-fn]
  (let [donkey (start-donkey-factory)]
    (reset! donkey-client (create-donkey-client donkey))
    (test-fn)
    (donkey-client/stop @donkey-client)
    @(donkey-core/destroy donkey)))
