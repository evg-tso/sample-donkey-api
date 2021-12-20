(ns sample-donkey-api.infrastructure.repository.kafka-producer
  (:require [sample-donkey-api.application.protocols :as protocols]
            [ketu.clients.producer :as kafka-producer]
            [integrant.core :as ig]
            [promesa.core :as p]
            [pronto.core :as pronto]))

(defn- create-callback-fn [deferred-promise]
  (kafka-producer/callback
    (fn [_ ex]
      (if (some? ex)
        (p/reject! deferred-promise ex)
        (p/resolve! deferred-promise true)))))

(deftype ^:private KafkaProducer [producer bytes->record]
  protocols/IMessagePublisher
  (publish-stock-order [_ proto-message]
    (let [deferred-promise (p/deferred)]
      (kafka-producer/send! producer
                            (-> proto-message pronto/proto-map->bytes bytes->record)
                            (create-callback-fn deferred-promise))
      deferred-promise))
  (close! [_]
    (kafka-producer/close! producer 5000)))

(defn- create-kafka-producer [stock-order-producer]
  (let [producer (kafka-producer/producer (:config stock-order-producer))]
    (KafkaProducer. producer (partial kafka-producer/record (:topic stock-order-producer)))))

(defmethod ig/init-key :repository/stock-order-producer [_ {:keys [config]}]
  (create-kafka-producer (-> config :kafka :stock-order :producer)))

(defmethod ig/halt-key! :repository/stock-order-producer [_ kafka-producer]
  (protocols/close! kafka-producer))
