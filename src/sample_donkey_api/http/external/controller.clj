(ns sample-donkey-api.http.external.controller
  (:require [sample-donkey-api.http.protocols :as protocols]
            [integrant.core :as ig]))

(def ^:private ^:const ok-response {:status 202})

(deftype ^:private HttpExternalController []
  protocols/IExternalController
  (order-stock [_ _]
    ok-response)
  (order-stock [_ _ respond _]
    (respond ok-response)))

(defmethod ig/init-key :external/controller [_ _]
  (HttpExternalController.))
