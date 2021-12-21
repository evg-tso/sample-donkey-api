(ns sample-donkey-api.http.middleware.exception
  (:require
    [com.brunobonacci.mulog :as logger]
    [reitit.ring.middleware.exception :as reitit-middleware-exception]))

(def ^:private ^:const generic-error-response
  {:status 500
   :body   (str
             "Something went wrong. "
             "Please try again in a few minutes.")})

(def exception-middleware
  (let [request-coercion-handler (reitit-middleware-exception/create-coercion-handler 400)]
    (reitit-middleware-exception/create-exception-middleware
      (merge
        reitit-middleware-exception/default-handlers
        {:reitit.ring.middleware.exception/default
         (fn [ex _]
           (logger/log ::unhandled-exception :exception ex)
           generic-error-response)

         :reitit.coercion/request-coercion
         (fn [ex request]
           (request-coercion-handler ex request))}))))
