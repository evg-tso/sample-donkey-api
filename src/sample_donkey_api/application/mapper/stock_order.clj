(ns sample-donkey-api.application.mapper.stock-order
  (:import (stocks StocksOuterClass$StockOrder StocksOuterClass$Direction StocksOuterClass$IP)))

(defn- string->direction-enum [s]
  (condp = s
    "buy" (StocksOuterClass$Direction/DIRECTION_BUY)
    "sell" (StocksOuterClass$Direction/DIRECTION_SELL)
    (StocksOuterClass$Direction/DIRECTION_UNSPECIFIED)))

(defn- ^StocksOuterClass$IP map->proto-ip [m]
  (-> (StocksOuterClass$IP/newBuilder)
      (.setCountryCode (:country_code m))
      (.setRegionCode (:region_code m))
      (.setLatitude (:latitude m))
      (.setLongitude (:longitude m))
      (.setContinentCode (:continent_code m))
      .build))

(defn request->proto [req]
  (let [builder (-> (StocksOuterClass$StockOrder/newBuilder)
                    (.setStockId (-> req :path-params :stock-id))
                    (.setAmountUsd (-> req :body-params :amount_usd))
                    (.setDirection (-> req :body-params :direction string->direction-enum)))]
    (when (some? (-> req :body-params :request_id))
      (.setRequestId builder (-> req :body-params :request_id)))
    (when (some? (-> req :sample/resolved-ip))
      (.setIp builder (-> req :sample/resolved-ip map->proto-ip)))
    (.build builder)))
