(ns sample-donkey-api.http.middleware.exception
  (:require [reitit.ring.middleware.exception :as reitit-middleware-exception]
            [com.brunobonacci.mulog :as logger]))

(def ^:private ^:const generic-error-response
  {:status 500
   :body   (str
             "Something went wrong. "
             "Please try again in a few minutes.")})

(def exception-middleware
  (reitit-middleware-exception/create-exception-middleware
    (merge
      reitit-middleware-exception/default-handlers
      {::reitit-middleware-exception/default
       (fn [ex _]
         (logger/log ::unhandled-exception :exception ex)
         generic-error-response)

       ::reitit-middleware-exception/wrap
       (fn [handler ex {:keys [uri] :as request}]
         (logger/log ::unhandled-exception :exception ex :uri {:uri uri})
         (handler ex request))})))
