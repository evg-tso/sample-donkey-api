(ns sample-donkey-api.integration.containers.all-test-containers
  (:require [clj-test-containers.core :as tc]
            [sample-donkey-api.integration.containers.kafka-setup :as kafka-setup]))

(defn- stop-containers! [containers]
  (run! tc/stop! containers))

(defn with-test-containers [test-fn]
  (let [kafka-container (kafka-setup/start-container)]
    (System/setProperty "kafka_stock.order_producer_config_brokers" (kafka-setup/get-bootstrap-servers kafka-container))
    (test-fn)
    (stop-containers! [kafka-container])))
