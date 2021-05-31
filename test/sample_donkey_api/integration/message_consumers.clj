(ns sample-donkey-api.integration.message-consumers
  (:require [sample-donkey-api.integration.containers.kafka-setup :as kafka-setup]
            [clojure.core.async :as async]
            [sample-donkey-api.utils.json :as json]
            [com.brunobonacci.mulog :as logger]))

(def stock-order-channel (async/chan 100 (map json/parse) #(logger/log ::error-mapping-kafka-message :exception %)))

(defn with-kafka-consumers [test-fn]
  (let [stock-order-consumer (kafka-setup/start-consuming stock-order-channel "stocks_orders")]
    (test-fn)
    (kafka-setup/stop-consuming! stock-order-consumer)))
