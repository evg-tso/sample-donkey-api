(ns sample-donkey-api.application.protocols)

(defprotocol IIPResolver
  (resolve-ip [this ip] "Returns the resolved IP as a Future of a map, or a completed Future"))

(defprotocol IMessagePublisher
  (publish-stock-order [this stock-order-proto-map] "Publish a stock order event, expected to return a truthy promise")
  (close! [this] "Closes the output"))
