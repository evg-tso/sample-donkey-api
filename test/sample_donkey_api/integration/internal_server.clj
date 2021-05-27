(ns sample-donkey-api.integration.internal-server
  ^:integration
  (:require [clojure.test :refer [deftest testing is use-fixtures]]
            [sample-donkey-api.integration.http-factory-setup :as http-factory-setup]
            [com.appsflyer.donkey.request :as donkey-request]
            [com.appsflyer.donkey.result :as donkey-result]
            [sample-donkey-api.assembly :as assembly]
            [com.appsflyer.donkey.client :as donkey-client]
            [com.brunobonacci.mulog :as logger]))

(defn- with-internal-server [test-fn]
  (let [system-map (assembly/start-application)]
    (test-fn)
    (assembly/stop-application system-map)))

(use-fixtures :once
              http-factory-setup/with-donkey-client
              with-internal-server)

(defn- do-get [url request]
  (->
    (donkey-client/request @http-factory-setup/donkey-client (assoc request :method :get
                                                                            :url url))
    donkey-request/submit
    (donkey-result/on-fail
      (fn [ex]
        (logger/log ::error-requesting-resource :exception ex :url url)
        (throw ex)))))

(deftest internal-apis
  (testing "that liveness request returns non-empty response"
    (is (= 200
           (-> @(do-get "http://localhost:8081/_/liveness" {})
               :status))))
  (testing "that readiness request returns non-empty response"
    (is (= 200
           (-> @(do-get "http://localhost:8081/_/readiness" {})
               :status)))))
