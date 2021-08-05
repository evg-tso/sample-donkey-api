(ns sample-donkey-api.infrastructure.repository.kafka-producer
  (:require [sample-donkey-api.application.protocols :as protocols]
            [ketu.async.sink :as sink]
            [clojure.core.async :as async]
            [com.brunobonacci.mulog :as logger]
            [integrant.core :as ig])
  (:import (com.google.protobuf Message)))

(deftype ^:private KafkaProducer [channel producer]
  protocols/IMessagePublisher
  (publish [_ message]
    (async/offer! channel message))
  (close! [_]
    (async/close! channel)))

(defn- create-kafka-producer [producer-config producer-channel-size-per-core]
  (let [available-processors (.availableProcessors (Runtime/getRuntime))
        channel-size         (* available-processors producer-channel-size-per-core)
        in-channel           (async/chan channel-size)
        serialized-channel   (async/chan channel-size)]
    (async/pipeline available-processors
                    serialized-channel
                    (map #(.toByteArray ^Message %))
                    in-channel
                    true
                    #(logger/log ::error-mapping-kafka-message :exception %))
    (KafkaProducer. in-channel (sink/sink serialized-channel producer-config))))

(defmethod ig/init-key :repository/stock-order-producer [_ {:keys [config]}]
  (create-kafka-producer (-> config :kafka :stock-order :producer :config)
                         (-> config :kafka :stock-order :producer :channel-size-per-core)))

(defmethod ig/halt-key! :repository/stock-order-producer [_ kafka-producer]
  (protocols/close! kafka-producer))
