(ns sample-donkey-api.http.external.routes
  (:require
    [integrant.core :as ig]
    [malli.util :as malli-util]
    [muuntaja.core :as muuntaja]
    [reitit.coercion.malli :as reitit-coercion-malli]
    [reitit.ring :as reitit-ring]
    [reitit.ring.coercion :as reitit-ring-coercion]
    [reitit.ring.middleware.muuntaja :as reitit-middleware-muuntaja]
    [reitit.swagger :as reitit-swagger]
    [reitit.swagger-ui :as reitit-swagger-ui]
    [ring.util.response :as response]
    [sample-donkey-api.http.middleware.exception :as exception-middleware]
    [sample-donkey-api.http.middleware.ip-resolver :as ip-resolver-middleware]
    [sample-donkey-api.http.protocols :as protocols]))

(defn- get-routes
  [{:keys [controller create-stock-order-schema ip-resolver]}]
  (let [responses {202 {:description "The request was accepted"}
                   400 {:description "In case any of the fields in the message body are missing, or if any of the fields are invalid"}
                   500 {:description "Internal server error"}}]
    [["" {:swagger {:id   :sample-donkey-api
                    :tags [:api]}}
      ["" {:no-doc true}
       ["/swagger.json"
        {:get {:swagger {:info {:title   "A sample http server"
                                :version "1.0.0"}}
               :handler (reitit-swagger/create-swagger-handler)}}]
       ["/api-docs/*"
        {:get (reitit-swagger-ui/create-swagger-ui-handler)}]]
      ["/api"
       ["/v1.0"
        ["/stocks/order/:stock-id" {:post {:summary    "Order stocks"
                                           :parameters {:path (malli-util/get create-stock-order-schema :path-params)
                                                        :body (malli-util/get create-stock-order-schema :body-params)}
                                           :responses  responses
                                           :middleware [(ip-resolver-middleware/create-ip-resolver-middleware ip-resolver)]
                                           :handler    (partial protocols/order-stock controller)}}]]]]]))

(defn- create-router
  "creates a reitit router and validates its structure"
  [routes]
  (reitit-ring/router
    routes
    {:data {:coercion   (reitit-coercion-malli/create
                          {:error-keys       #{:humanized}
                           :compile          malli-util/open-schema
                           :validate         true
                           :enabled          true
                           :encode-error     (fn [validation-body] {:errors (:humanized validation-body)})
                           :default-values   false
                           :strip-extra-keys true})
            :muuntaja   (-> muuntaja/default-options
                            (update :formats select-keys ["application/json"])
                            (assoc-in [:formats "application/json" :decoder-opts :bigdecimals] true)
                            (assoc :return :bytes)
                            muuntaja/create)
            :middleware [reitit-swagger/swagger-feature
                         reitit-middleware-muuntaja/format-middleware
                         exception-middleware/exception-middleware
                         reitit-ring-coercion/coerce-request-middleware]}}))

(defn- create-ring-handler [router]
  (reitit-ring/ring-handler
    router
    (reitit-ring/create-default-handler
      {:not-found (constantly (response/status 404))})))

(defn- routes [controller create-stock-order-schema ip-resolver]
  (let [ring-handler (-> (get-routes {:controller                controller
                                      :create-stock-order-schema create-stock-order-schema
                                      :ip-resolver               ip-resolver})
                         create-router
                         create-ring-handler)]
    [{:handler      ring-handler
      :handler-mode :non-blocking}]))

(defmethod ig/init-key :external/routes [_ {:keys [controller create-stock-order-schema ip-resolver]}]
  (routes controller create-stock-order-schema ip-resolver))
