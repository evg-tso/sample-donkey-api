(ns sample-donkey-api.http.protocols)

(defprotocol IInternalController
  "Defines the API of the internal server"
  (liveness [this req] [this req respond raise])
  (readiness [this req] [this req respond raise]))
