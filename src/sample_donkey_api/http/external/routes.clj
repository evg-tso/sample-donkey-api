(ns sample-donkey-api.http.external.routes
  (:require [integrant.core :as ig]
            [reitit.ring :as reitit-ring]
            [ring.util.response :as response]
            [reitit.swagger :as reitit-swagger]
            [reitit.swagger-ui :as reitit-swagger-ui]
            [malli.util :as malli-util]
            [reitit.ring.middleware.muuntaja :as reitit-middleware-muuntaja]
            [sample-donkey-api.http.middleware.exception :as exception-middleware]
            [sample-donkey-api.http.middleware.validation :as validation-middleware]
            [sample-donkey-api.http.middleware.ip-resolver :as ip-resolver-middleware]
            [muuntaja.core :as muuntaja]
            [sample-donkey-api.http.protocols :as protocols]
            [reitit.coercion.malli :as reitit-coercion-malli]))

(defn- get-routes
  [{:keys [controller create-stock-order-schema validation-service ip-resolver]}]
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
                                           :middleware [(validation-middleware/create-validate-request-middleware validation-service)
                                                        (ip-resolver-middleware/create-ip-resolver-middleware ip-resolver)]
                                           :handler    (partial protocols/order-stock controller)}}]]]]]))

(defn- create-router
  "creates a reitit router and validates its structure"
  [routes]
  (reitit-ring/router
    routes
    {:data {:coercion   (reitit-coercion-malli/create
                          {:error-keys       #{:humanized}
                           :compile          malli-util/open-schema
                           :strip-extra-keys false})
            :muuntaja   (-> muuntaja/default-options
                            (update :formats select-keys ["application/json"])
                            muuntaja/create)
            :middleware [reitit-swagger/swagger-feature
                         exception-middleware/exception-middleware
                         reitit-middleware-muuntaja/format-middleware]}}))

(defn- create-ring-handler [router]
  (reitit-ring/ring-handler
    router
    (reitit-ring/create-default-handler
      {:not-found (constantly (response/status 404))})))

(defn- routes [controller create-stock-order-schema validation-service ip-resolver]
  (let [ring-handler (-> (get-routes {:controller                controller
                                      :create-stock-order-schema create-stock-order-schema
                                      :validation-service        validation-service
                                      :ip-resolver               ip-resolver})
                         create-router
                         create-ring-handler)]
    [{:handler      ring-handler
      :handler-mode :non-blocking}]))

(defmethod ig/init-key :external/routes [_ {:keys [controller create-stock-order-schema validation-service ip-resolver]}]
  (routes controller create-stock-order-schema validation-service ip-resolver))
