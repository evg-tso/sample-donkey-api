(ns sample-donkey-api.http.middleware.ip-resolver
  (:require [sample-donkey-api.application.protocols :as protocols]
            [promesa.core :as p]
            [com.brunobonacci.mulog :as logger]))

(defn create-ip-resolver-middleware [ip-resolver]
  (fn [handler]
    (fn [request respond raise]
      (let [ip (-> request :parameters :body :ip)]
        (-> (protocols/resolve-ip ip-resolver ip)
            (p/then (fn [resolved-ip]
                      (if (some? resolved-ip)
                        (handler (assoc request :sample/resolved-ip resolved-ip) respond raise)
                        (handler request respond raise))))
            (p/catch (fn [ex]
                       (logger/log ::error-resolving-ip :exception ex :ip ip)
                       (handler request respond raise))))))))
