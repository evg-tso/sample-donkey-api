(ns sample-donkey-api.infrastructure.repository.ip-resolver
  (:require [sample-donkey-api.application.protocols :as protocols]
            [com.appsflyer.donkey.client :as donkey-client]
            [com.appsflyer.donkey.request :as donkey-request]
            [com.appsflyer.donkey.result :as donkey-result]
            [com.brunobonacci.mulog :as logger]
            [sample-donkey-api.utils.json :as json]
            [integrant.core :as ig]
            [promesa.core :as p]))

(defn- resolve-ip-by-http [ip http-client access-key url-template]
  (let [future-promise (p/deferred)
        url            (format url-template ip access-key)]
    (-> http-client
        (donkey-client/request {:url url :method :get})
        donkey-request/submit
        (donkey-result/on-success
          (fn [res]
            (p/resolve! future-promise res)))
        (donkey-result/on-fail
          (fn [ex]
            (logger/log ::error-resolving-ip :exception ex :url url)
            (p/reject! future-promise ex))))
    future-promise))

(deftype ^:private IPStackResolver [http-client access-key url-template]
  protocols/IIPResolver
  (resolve-ip [_ ip]
    (-> (resolve-ip-by-http ip http-client access-key url-template)
        (p/then (fn [response]
                  (when (= (:status response) 200)
                    (-> response :body json/parse)))))))

(defmethod ig/init-key :repository/ip-resolver [_ {:keys [http-client config]}]
  (IPStackResolver. http-client (-> config :ip-resolver :access-key)
                    (-> config :ip-resolver :url-template)))
