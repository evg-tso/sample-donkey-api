(ns sample-donkey-api.integration.containers.kafka-setup
  (:require [com.brunobonacci.mulog :as logger]
            [clj-test-containers.core :as tc]
            [clj-uuid :as uuid]
            [jackdaw.client :as jc]
            [clojure.core.async :as async])
  (:import (org.testcontainers.containers KafkaContainer)
           (org.testcontainers.utility DockerImageName)))

(def ^:private kafka-container (atom nil))
(def ^:private ^:const container-port 9093)

(defn get-bootstrap-servers [container]
  (.getBootstrapServers ^KafkaContainer (:container container)))

(defn start-container []
  (logger/log ::creating-kafka-container)
  (let [container (-> {:container     (KafkaContainer. (DockerImageName/parse "confluentinc/cp-kafka:6.1.1"))
                       :exposed-ports [container-port]}
                      tc/init
                      tc/start!)]
    (reset! kafka-container container)))

(defn- poll-and-loop!
  [consumer processing-fn continue?]
  (let [poll-ms 200]
    (loop []
      (when (true? @continue?)
        (let [records (jc/poll consumer poll-ms)]
          (when (seq records)
            (doseq [record records]
              (processing-fn record))
            (.commitSync consumer))
          (recur))))))

(defn- process-messages! [continue? consumer-config topic-name processing-fn]
  (future
    (with-open [consumer (jc/subscribed-consumer consumer-config [{:topic-name topic-name}])]
      (poll-and-loop! consumer processing-fn continue?))))

(defn start-consuming
  "Starting consumption from the `topic` topic, putting the value of the records into the `channel` channel.
   Returns a functions to stop the consumer."
  [channel topic]
  (let [continue?       (atom true)
        consumer-id     (str "test-consumer-" (uuid/v4))
        consumer-config {"bootstrap.servers"  (get-bootstrap-servers @kafka-container)
                         "group.id"           consumer-id
                         "key.deserializer"   "org.apache.kafka.common.serialization.ByteArrayDeserializer"
                         "value.deserializer" "org.apache.kafka.common.serialization.ByteArrayDeserializer"}]
    (process-messages! continue? consumer-config topic #(async/>!! channel %))
    (fn stop-consuming! []
      (reset! continue? false)
      (Thread/sleep 200))))
