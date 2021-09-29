(ns sample-donkey-api.application.mapper.proto-definitions
  (:require [pronto.core :as pronto]
            [pronto.utils :as pronto-utils])
  (:import (stocks StocksOuterClass$StockOrder StocksOuterClass$IP)))

(pronto/defmapper proto-mapper
                  [StocksOuterClass$StockOrder
                   StocksOuterClass$IP]
                  :key-name-fn pronto-utils/->kebab-case
                  :enum-value-fn pronto-utils/->kebab-case)
