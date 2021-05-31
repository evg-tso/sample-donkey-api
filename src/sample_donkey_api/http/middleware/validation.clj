(ns sample-donkey-api.http.middleware.validation
  (:require [sample-donkey-api.application.protocols :as protocols]))

(defn create-validate-request-middleware [validation-service]
  (fn [handler]
    (fn [request respond raise]
      (if (protocols/valid? validation-service request)
        (handler request respond raise)
        (respond {:status 400 :body {:errors (protocols/explain validation-service request)}})))))
