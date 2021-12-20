(ns sample-donkey-api.integration.logs-setup
  (:require
    [com.brunobonacci.mulog :as logger]
    [org.slf4j.impl.mulog]))

(defn with-logs [test-fn]
  (logger/start-publisher! {:type :console})
  (test-fn))
