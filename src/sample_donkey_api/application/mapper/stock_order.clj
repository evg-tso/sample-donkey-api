(ns sample-donkey-api.application.mapper.stock-order
  (:require
    [pronto.core :as pronto]
    [sample-donkey-api.application.mapper.proto-definitions :as proto-defs])
  (:import
    (stocks
      StocksOuterClass$IP
      StocksOuterClass$StockOrder)))

(defn- string->direction-keyword [s]
  (condp = s
    "buy" :direction-buy
    "sell" :direction-sell
    :direction-unspecified))

(defn- map->proto-ip [m]
  (pronto/proto-map proto-defs/proto-mapper StocksOuterClass$IP
                    :country-code (:country_code m)
                    :region-code (:region_code m)
                    :latitude (:latitude m)
                    :longitude (:longitude m)
                    :continent-code (:continent_code m)))

(defn request->proto-bytes [req]
  (let [parameters (:parameters req)
        body       (:body parameters)
        path       (:path parameters)]
    (pronto/pcond-> (pronto/proto-map proto-defs/proto-mapper
                                      StocksOuterClass$StockOrder
                                      :amount-usd (.doubleValue ^BigDecimal (:amount_usd body))
                                      :stock-id (:stock-id path)
                                      :direction (-> body :direction string->direction-keyword))
                    (some? (:request_id body)) (assoc :request-id (-> body :request_id str))
                    (some? (:sample/resolved-ip req)) (assoc :ip (-> req :sample/resolved-ip map->proto-ip)))))
