(ns sample-donkey-api.integration.internal-server
  (:require
    [clojure.test :refer [deftest testing is use-fixtures]]
    [com.appsflyer.donkey.client :as donkey-client]
    [com.appsflyer.donkey.request :as donkey-request]
    [com.appsflyer.donkey.result :as donkey-result]
    [com.brunobonacci.mulog :as logger]
    [sample-donkey-api.assembly :as assembly]
    [sample-donkey-api.integration.containers.all-test-containers :as all-test-containers]
    [sample-donkey-api.integration.http-factory-setup :as http-factory-setup]
    [sample-donkey-api.integration.logs-setup :as logs-setup]))

(defn- with-internal-server [test-fn]
  (let [system-map (assembly/start-application)]
    (test-fn)
    (assembly/stop-application system-map)))

(use-fixtures :once
              logs-setup/with-logs
              http-factory-setup/with-donkey-client
              all-test-containers/with-test-containers
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
