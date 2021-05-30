(ns sample-donkey-api.integration.logs-setup
  (:require [org.slf4j.impl.mulog]
            [com.brunobonacci.mulog :as logger]))

(defn with-logs [test-fn]
  (logger/start-publisher! {:type :console})
  (test-fn))