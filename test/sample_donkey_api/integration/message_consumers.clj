(ns sample-donkey-api.integration.message-consumers
  (:require [sample-donkey-api.integration.containers.kafka-setup :as kafka-setup]
            [clojure.core.async :as async]
            [com.brunobonacci.mulog :as logger]
            [clojure.string :as s])
  (:import (stocks StocksOuterClass$StockOrder StocksOuterClass$IP)))

(def stock-order-channel (async/chan 100
                                     (comp
                                       (map #(StocksOuterClass$StockOrder/parseFrom ^bytes %))
                                       (map (fn [^StocksOuterClass$StockOrder stock-order]
                                              {:amount_usd (.getAmountUsd stock-order)
                                               :request_id (.getRequestId stock-order)
                                               :ip         {:continent_code (-> stock-order ^StocksOuterClass$IP .getIp .getContinentCode)
                                                            :country_code   (-> stock-order ^StocksOuterClass$IP .getIp .getCountryCode)
                                                            :latitude       (-> stock-order ^StocksOuterClass$IP .getIp .getLatitude)
                                                            :longitude      (-> stock-order ^StocksOuterClass$IP .getIp .getLongitude)
                                                            :region_code    (-> stock-order ^StocksOuterClass$IP .getIp .getRegionCode)}
                                               :direction  (-> stock-order .getDirection .name s/lower-case)
                                               :stock_id   (.getStockId stock-order)})))
                                     #(logger/log ::error-mapping-kafka-message :exception %)))

(defn with-kafka-consumers [test-fn]
  (let [stock-order-consumer (kafka-setup/start-consuming stock-order-channel "stocks_orders")]
    (test-fn)
    (kafka-setup/stop-consuming! stock-order-consumer)))
