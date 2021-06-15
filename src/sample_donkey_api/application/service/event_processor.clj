(ns sample-donkey-api.application.service.event-processor
  (:require [integrant.core :as ig]
            [com.brunobonacci.mulog :as logger]
            [sample-donkey-api.application.mapper.stock-order :as stock-order-mapper]
            [sample-donkey-api.application.protocols :as protocols]))

(def ^:private ^:const SUCCESS-RESULT {:result :success})
(def ^:private ^:const RESOURCE-EXHAUSTED-RESULT {:result :failure
                                                  :reason :resource-exhausted})
(def ^:private ^:const ERROR-RESULT {:result :failure
                                     :reason :error})

(defn- decorate-with-try [mapper-fn]
  (fn [req]
    (try
      (mapper-fn req)
      (catch Throwable ex
        (logger/log ::error-mapping-request :exception ex)
        nil))))

(defn map-and-put-fn
  "Returns a pure function of type 'req -> result' that maps the request to another map
   and puts in the output-chan.
   The result will be a {:result :success} map for successes
   or for failures a {:result :failure :reason :SOME_KEYWORD}"
  [mapper-fn message-producer]
  (let [mapper-fn (decorate-with-try mapper-fn)]
    (fn [req]
      (if-let [proto-class (mapper-fn req)]
        (if (protocols/publish message-producer proto-class)
          SUCCESS-RESULT
          RESOURCE-EXHAUSTED-RESULT)
        ERROR-RESULT))))

(defmethod ig/init-key :processor/stock-order [_ {:keys [message-producer]}]
  (map-and-put-fn stock-order-mapper/request->proto message-producer))
