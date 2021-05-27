(ns sample-donkey-api.http.middleware.validation
  (:require [sample-donkey-api.application.protocols :as protocols]))

(defn validate-request-middleware [handler validation-service]
  (fn
    ([request]
     (if (protocols/valid? validation-service request)
       (handler request)
       {:status 400 :body {:errors (protocols/explain validation-service request)}}))
    ([request respond raise]
     (if (protocols/valid? validation-service request)
       (handler request respond raise)
       (respond {:status 400 :body {:errors (protocols/explain validation-service request)}})))))
