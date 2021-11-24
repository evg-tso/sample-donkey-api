(ns sample-donkey-api.infrastructure.repository.kafka-producer
  (:require [sample-donkey-api.application.protocols :as protocols]
            [ketu.clients.producer :as kafka-producer]
            [integrant.core :as ig]
            [promesa.core :as p]))

(deftype ^:private KafkaProducer [producer bytes->record]
  protocols/IMessagePublisher
  (publish-stock-order [_ message-bytes]
    (-> (kafka-producer/send! producer (bytes->record message-bytes))
        (p/then (constantly true))))
  (close! [_]
    (kafka-producer/close! producer 5000)))

(defn- create-kafka-producer [stock-order-producer]
  (let [producer (kafka-producer/producer (:config stock-order-producer))]
    (KafkaProducer. producer (partial kafka-producer/record (:topic stock-order-producer)))))

(defmethod ig/init-key :repository/stock-order-producer [_ {:keys [config]}]
  (create-kafka-producer (-> config :kafka :stock-order :producer)))

(defmethod ig/halt-key! :repository/stock-order-producer [_ kafka-producer]
  (protocols/close! kafka-producer))
