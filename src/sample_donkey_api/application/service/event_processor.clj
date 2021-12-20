(ns sample-donkey-api.application.service.event-processor
  (:require [integrant.core :as ig]
            [com.brunobonacci.mulog :as logger]
            [sample-donkey-api.application.mapper.stock-order :as stock-order-mapper]
            [sample-donkey-api.application.protocols :as protocols]
            [promesa.core :as p]))

(def ^:private ^:const SUCCESS-RESULT {:result :success})
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
  "Returns a pure function of type 'req -> result' that maps the request to proto bytes
   and publishes the proto bytes to the supplied message-producer.
   The result will be a future of {:result :success} map for successes
   or for failures a future of {:result :failure :reason :error}"
  [mapper-fn message-producer]
  (let [mapper-fn (decorate-with-try mapper-fn)]
    (fn [req]
      (if-let [proto-map (mapper-fn req)]
        (-> (protocols/publish-stock-order message-producer proto-map)
            (p/then (constantly SUCCESS-RESULT))
            (p/catch (constantly ERROR-RESULT)))
        (p/resolved ERROR-RESULT)))))

(defmethod ig/init-key :processor/stock-order [_ {:keys [message-producer]}]
  (map-and-put-fn stock-order-mapper/request->proto-bytes message-producer))
