(ns sample-donkey-api.integration.external-server
  ^:integration
  (:require [clojure.test :refer [deftest testing is use-fixtures]]
            [sample-donkey-api.assembly :as assembly]
            [sample-donkey-api.integration.http-factory-setup :as http-factory-setup]
            [com.appsflyer.donkey.client :as donkey-client]
            [com.appsflyer.donkey.request :as donkey-request]
            [com.appsflyer.donkey.result :as donkey-result]
            [sample-donkey-api.integration.order-stock-messages :as order-stock-messages]
            [com.brunobonacci.mulog :as logger]
            [sample-donkey-api.json :as json]))

(defn- with-external-server [test-fn]
  (let [system-map (assembly/start-application)]
    (test-fn)
    (assembly/stop-application system-map)))

(use-fixtures :once
              http-factory-setup/with-donkey-client
              with-external-server)

(defn- do-post [url request]
  (->
    (donkey-client/request @http-factory-setup/donkey-client (assoc request :method :post
                                                                            :url url))
    (donkey-request/submit (:body request))
    (donkey-result/on-fail
      (fn [ex]
        (logger/log ::error-requesting-resource :exception ex :url url)
        (throw ex)))))

(deftest order-stocks-route-tests
  (testing "that a valid request is accepted"
    (let [request  (order-stock-messages/generate-request)
          response @(do-post order-stock-messages/url request)]
      (is (= (:status response)
             202))))
  (testing "that an empty payload returns 400 and shows all the invalid fields"
    (let [request  (order-stock-messages/generate-request {})
          response @(do-post order-stock-messages/url request)]
      (is (= (:status response)
             400))
      (is (= (-> response :body json/parse)
             {:errors {:body-params {:amount_usd ["missing required key"]
                                     :direction  ["missing required key"]
                                     :ip         ["missing required key"]}}})))))
