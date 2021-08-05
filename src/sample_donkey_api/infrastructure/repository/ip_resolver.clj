(ns sample-donkey-api.infrastructure.repository.ip-resolver
  (:require [sample-donkey-api.application.protocols :as protocols]
            [com.appsflyer.donkey.client :as donkey-client]
            [com.appsflyer.donkey.request :as donkey-request]
            [com.appsflyer.donkey.result :as donkey-result]
            [com.brunobonacci.mulog :as logger]
            [sample-donkey-api.utils.json :as json]
            [clojure.core.async :as async]
            [integrant.core :as ig]))

(defn- resolve-ip-by-http [ip http-client access-key url-template]
  (let [promise-chan (async/promise-chan)
        url          (format url-template ip access-key)]
    (-> http-client
        (donkey-client/request {:url url :method :get})
        donkey-request/submit
        (donkey-result/on-success
          (fn [res]
            (async/>!! promise-chan {:res res})))
        (donkey-result/on-fail
          (fn [ex]
            (logger/log ::error-resolving-ip :exception ex :url url)
            (async/>!! promise-chan {:error ex}))))
    promise-chan))

(deftype ^:private IPStackResolver [http-client access-key url-template]
  protocols/IIPResolver
  (resolve-ip [_ ip]
    (let [resolved-ip-chan (resolve-ip-by-http ip http-client access-key url-template)]
      (async/go
        (some-> resolved-ip-chan
                async/<!
                :res
                (as-> response (when (= (:status response) 200) response))
                :body
                json/parse)))))

(defmethod ig/init-key :repository/ip-resolver [_ {:keys [http-client config]}]
  (IPStackResolver. http-client (-> config :ip-resolver :access-key)
                    (-> config :ip-resolver :url-template)))
