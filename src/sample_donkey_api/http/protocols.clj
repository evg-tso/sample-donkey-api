(ns sample-donkey-api.http.protocols)

(defprotocol IExternalController
  (ping [this req] [this req respond raise]))

(defprotocol IInternalController
  (liveness [this req] [this req respond raise])
  (readiness [this req] [this req respond raise]))
