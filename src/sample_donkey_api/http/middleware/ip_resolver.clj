(ns sample-donkey-api.http.middleware.ip-resolver
  (:require [sample-donkey-api.application.protocols :as protocols]
            [promesa.core :as p]))

(defn create-ip-resolver-middleware [ip-resolver]
  (fn [handler]
    (fn [request respond raise]
      (-> (protocols/resolve-ip ip-resolver (-> request :body-params :ip))
          (p/then (fn [resolved-ip]
                    (if (some? resolved-ip)
                      (handler (assoc request :sample/resolved-ip resolved-ip) respond raise)
                      (handler request respond raise))))))))
