(ns sample-donkey-api.application.protocols)

(defprotocol IValidationService
  (valid? [this request] "Validates the request and returns true/false")
  (explain [this request] "If the request is invalid, returns a map of all the field with errors"))

(defprotocol IIPResolver
  (resolve-ip [this ip] "Returns the resolved IP as a Future of a map, or a completed Future"))

(defprotocol IMessagePublisher
  (publish-stock-order [this stock-order-proto-bytes] "Publish a stock order event, expected to return a truthy promise")
  (close! [this] "Closes the output"))
