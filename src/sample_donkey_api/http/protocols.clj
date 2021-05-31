(ns sample-donkey-api.http.protocols)

(defprotocol IExternalController
  (order-stock [this req] [this req respond raise]))

(defprotocol IInternalController
  (liveness [this req respond raise])
  (readiness [this req respond raise]))
