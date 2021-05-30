(ns sample-donkey-api.application.protocols)

(defprotocol IValidationService
  (valid? [this request] "Validates the request and returns true/false")
  (explain [this request] "If the request is invalid, returns a map of all the field with errors"))

(defprotocol IIPResolver
  (resolve-ip [this ip] "Returns the resolved IP as a map, or nil"))
