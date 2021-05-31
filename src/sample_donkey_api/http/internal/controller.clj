(ns sample-donkey-api.http.internal.controller
  (:require [sample-donkey-api.http.protocols :as protocols]
            [integrant.core :as ig]))

(def ^:private ^:const ok-response {:status 200})

(deftype ^:private HttpInternalController []
  protocols/IInternalController
  (liveness [_ _ respond _]
    (respond ok-response))
  (readiness [_ _ respond _]
    (respond ok-response)))

(defmethod ig/init-key :internal/controller [_ _]
  (HttpInternalController.))
