(ns sample-donkey-api.http.internal.controller
  (:require
    [integrant.core :as ig]
    [sample-donkey-api.http.protocols :as protocols]))

(def ^:private ^:const ok-response {:status 200})

(deftype ^:private HttpInternalController []
  protocols/IInternalController
  (liveness [_ _ respond _]
    (respond ok-response))
  (readiness [_ _ respond _]
    (respond ok-response)))

(defmethod ig/init-key :internal/controller [_ _]
  (HttpInternalController.))
