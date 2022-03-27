(ns sample-donkey-api.integration.containers.kafka-setup
  (:require
    [clj-test-containers.core :as tc]
    [com.brunobonacci.mulog :as logger]
    [ketu.async.source :as source])
  (:import
    (org.testcontainers.containers
      KafkaContainer)
    (org.testcontainers.utility
      DockerImageName)))

(def ^:private kafka-container (atom nil))
(def ^:private ^:const container-port 9093)

(defn get-bootstrap-servers [container]
  (.getBootstrapServers ^KafkaContainer (:container container)))

(defn start-container []
  (logger/log ::creating-kafka-container)
  (let [container (-> {:container     (KafkaContainer. (DockerImageName/parse "confluentinc/cp-kafka:5.5.3"))
                       :exposed-ports [container-port]}
                      tc/init
                      tc/start!)]
    (reset! kafka-container container)))

(defn start-consuming [channel topic]
  (let [consumer-id (str "test-consumer-" (random-uuid))]
    (source/source
      channel
      {:name            consumer-id
       :topic           topic
       :group-id        consumer-id
       :brokers         (get-bootstrap-servers @kafka-container)
       :value-type      :byte-array
       :shape           :value
       :internal-config {"auto.offset.reset" "latest"}})))

(defn stop-consuming! [consumer]
  (source/stop! consumer))
