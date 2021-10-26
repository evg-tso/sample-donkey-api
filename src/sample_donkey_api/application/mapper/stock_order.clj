(ns sample-donkey-api.application.mapper.stock-order
  (:require [pronto.core :as pronto]
            [sample-donkey-api.application.mapper.proto-definitions :as proto-defs])
  (:import (stocks StocksOuterClass$StockOrder StocksOuterClass$IP)))

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
  (pronto/pcond-> (pronto/proto-map proto-defs/proto-mapper
                                    StocksOuterClass$StockOrder
                                    :amount-usd (-> req :body-params :amount_usd)
                                    :stock-id (-> req :path-params :stock-id)
                                    :direction (-> req :body-params :direction string->direction-keyword))
                  (some? (-> req :body-params :request_id)) (assoc :request-id (-> req :body-params :request_id))
                  (some? (-> req :sample/resolved-ip)) (assoc :ip (-> req :sample/resolved-ip map->proto-ip))
                  true pronto/proto-map->bytes))
