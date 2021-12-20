(ns sample-donkey-api.integration.message-consumers
  (:require [sample-donkey-api.integration.containers.kafka-setup :as kafka-setup]
            [clojure.core.async :as async]
            [com.brunobonacci.mulog :as logger]
            [pronto.core :as pronto]
            [sample-donkey-api.application.mapper.proto-definitions :as proto-defs])
  (:import (stocks StocksOuterClass$StockOrder)))

(def stock-order-channel (async/chan 100
                                     (map #(pronto/bytes->proto-map proto-defs/proto-mapper StocksOuterClass$StockOrder %))
                                     #(logger/log ::error-mapping-kafka-message :exception %)))

(defn with-kafka-consumers [test-fn]
  (let [stock-order-consumer (kafka-setup/start-consuming stock-order-channel "stocks_orders")]
    (async/<!! (async/timeout 2000))
    (test-fn)
    (kafka-setup/stop-consuming! stock-order-consumer)))
