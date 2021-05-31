(ns sample-donkey-api.http.external.controller
  (:require [sample-donkey-api.http.protocols :as protocols]
            [integrant.core :as ig]))

(def ^:private ^:const accepted-response {:status 202})
(def ^:private ^:const internal-error-response {:status 500})

(defn- handle-result [result]
  (if (= (:result result) :success)
    accepted-response
    internal-error-response))

(deftype ^:private HttpExternalController [stock-order-processor]
  protocols/IExternalController
  (order-stock [_ _]
    accepted-response)
  (order-stock [_ req respond _]
    (-> req
        stock-order-processor
        handle-result
        respond)))

(defmethod ig/init-key :external/controller [_ {:keys [stock-order-processor]}]
  (HttpExternalController. stock-order-processor))
