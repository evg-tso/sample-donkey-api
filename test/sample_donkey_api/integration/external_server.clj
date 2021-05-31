(ns sample-donkey-api.integration.external-server
  (:require [clojure.test :refer [deftest testing is use-fixtures]]
            [sample-donkey-api.assembly :as assembly]
            [sample-donkey-api.integration.http-factory-setup :as http-factory-setup]
            [com.appsflyer.donkey.client :as donkey-client]
            [com.appsflyer.donkey.request :as donkey-request]
            [com.appsflyer.donkey.result :as donkey-result]
            [sample-donkey-api.integration.order-stock-messages :as order-stock-messages]
            [com.brunobonacci.mulog :as logger]
            [sample-donkey-api.integration.message-consumers :as message-consumers]
            [sample-donkey-api.utils.json :as json]
            [sample-donkey-api.integration.containers.all-test-containers :as all-test-containers]
            [clojure.core.async :as async]
            [sample-donkey-api.integration.logs-setup :as logs-setup]))

(defn- with-external-server [test-fn]
  (let [system-map (assembly/start-application)]
    (test-fn)
    (assembly/stop-application system-map)))

(use-fixtures :once
              logs-setup/with-logs
              http-factory-setup/with-donkey-client
              all-test-containers/with-test-containers
              message-consumers/with-kafka-consumers
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

(defn <!!?
  "Reads from chan synchronously, waiting for a given maximum of milliseconds.
  If the value does not come in during that period, returns :timed-out. If
  milliseconds is not given, a default of 1000 is used."
  ([chan]
   (<!!? chan 1000))
  ([chan milliseconds]
   (let [timeout (async/timeout milliseconds)
         [value port] (async/alts!! [chan timeout])]
     (if (= chan port)
       value
       :timed-out))))

(deftest order-stocks-route-tests
  (testing "that a valid request is accepted"
    (let [request       (order-stock-messages/generate-request)
          response      @(do-post order-stock-messages/url request)
          kafka-message (<!!? message-consumers/stock-order-channel)]
      (is (= (:status response)
             202))
      (is (= order-stock-messages/expected-msg-in-chan
             kafka-message))))
  (testing "that an empty payload returns 400 and shows all the invalid fields"
    (let [request  (order-stock-messages/generate-request {})
          response @(do-post order-stock-messages/url request)]
      (is (= (:status response)
             400))
      (is (= (-> response :body json/parse)
             {:errors {:body-params {:amount_usd ["missing required key"]
                                     :direction  ["missing required key"]
                                     :ip         ["missing required key"]}}})))))
