(ns sample-donkey-api.core
  (:gen-class)
  (:require [com.brunobonacci.mulog :as logger]
            [sample-donkey-api.assembly :as assembly]))

(defn- shutdown-hook [shutdown-system-fn]
  (fn []
    (try
      (logger/log ::shutdown-started)
      (shutdown-system-fn)
      (logger/log ::shutdown-completed)
      (catch Exception ex
        (logger/log ::shutdown-failed :exception ex))
      (finally (flush)))))

(defn- add-shutdown-hook [shutdown-system-fn]
  (let [hook (Thread. ^Runnable (shutdown-hook shutdown-system-fn))]
    (.setName hook "shutdown-hook")
    (-> (Runtime/getRuntime)
        (.addShutdownHook hook))))

(defn -main  []
  (try
    (logger/start-publisher! {:type :console})
    (let [system-map (assembly/start-application)]
      (add-shutdown-hook #(assembly/stop-application system-map))
      (logger/log ::service-started :system-map system-map)
      system-map)
    (catch Exception ex
      (logger/log :service-could-not-start :exception ex))))
