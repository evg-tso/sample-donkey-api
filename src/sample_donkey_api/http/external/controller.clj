(ns sample-donkey-api.http.external.controller
  (:require [sample-donkey-api.http.protocols :as protocols]
            [integrant.core :as ig]))

(def ^:private ^:const ok-response {:status 200 :body "pong"})

(deftype ^:private HttpExternalController []
  protocols/IExternalController
  (ping [_ _]
    ok-response)
  (ping [_ _ respond _]
    (respond ok-response)))

(defmethod ig/init-key :external/controller [_ _]
  (->HttpExternalController))
