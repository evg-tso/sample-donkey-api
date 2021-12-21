(ns sample-donkey-api.http.external.controller
  (:require
    [integrant.core :as ig]
    [promesa.core :as p]
    [sample-donkey-api.http.protocols :as protocols]))

(def ^:private ^:const accepted-response {:status 202})
(def ^:private ^:const internal-error-response {:status 500})

(defn- handle-result [result]
  (if (= (:result result) :success)
    accepted-response
    internal-error-response))

(deftype ^:private HttpExternalController [stock-order-processor]
  protocols/IExternalController
  (order-stock [_ req respond _]
    (-> (stock-order-processor req)
        (p/then (fn [result]
                  (respond (handle-result result)))))))

(defmethod ig/init-key :external/controller [_ {:keys [stock-order-processor]}]
  (HttpExternalController. stock-order-processor))
