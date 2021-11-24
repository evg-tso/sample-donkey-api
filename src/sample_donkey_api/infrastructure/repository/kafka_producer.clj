(ns sample-donkey-api.infrastructure.repository.kafka-producer
  (:require [sample-donkey-api.application.protocols :as protocols]
            [jackdaw.client :as jc]
            [integrant.core :as ig]
            [promesa.core :as p])
  (:import (org.apache.kafka.clients.producer Producer ProducerRecord)
           (java.time Duration)))

(defn- bytes->ProducerRecord [topic-name ^bytes message-bytes]
  (ProducerRecord. topic-name message-bytes))

(deftype ^:private KafkaProducer [producer bytes->ProducerRecord-fn]
  protocols/IMessagePublisher
  (publish-stock-order [_ message-bytes]
    (-> (jc/send! producer ^ProducerRecord (bytes->ProducerRecord-fn message-bytes))
        (p/then (constantly true))))
  (close! [_]
    (.close ^Producer producer (Duration/ofSeconds 5))))

(defn- create-kafka-producer [stock-order-producer]
  (let [producer (jc/producer (:config stock-order-producer))]
    (KafkaProducer. producer (partial bytes->ProducerRecord (:topic stock-order-producer)))))

(defmethod ig/init-key :repository/stock-order-producer [_ {:keys [config]}]
  (create-kafka-producer (-> config :kafka :stock-order :producer)))

(defmethod ig/halt-key! :repository/stock-order-producer [_ kafka-producer]
  (protocols/close! kafka-producer))
