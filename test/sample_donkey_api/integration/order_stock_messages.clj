(ns sample-donkey-api.integration.order-stock-messages
  (:require [sample-donkey-api.utils.json :as json]))

(def ^:private stock-id "AAPL")
(def url (str "http://localhost:8080/api/v1.0/stocks/order/" stock-id))

(def valid-payload
  {:amount_usd 3.47
   :request_id "71dad7da-7926-40d8-9b15-b94a6d46e15a"
   :ip         "35.244.183.10"
   :direction  "buy"})

(defn generate-request
  ([]
   (generate-request valid-payload))
  ([payload]
   {:body    (json/stringify payload)
    :headers {"host"              "my.domain.com"
              "Accept-Encoding"   "gzip"
              "Content-Type"      "application/json"
              "User-Agent"        "Dalvik/2.1.0 (Linux; U; Android 5.1; HUAWEI LUA-U22 Build/HUAWEILUA-U22)"
              "X-Forwarded-For"   "78.95.127.42"
              "X-Forwarded-Port"  443
              "X-Forwarded-Proto" "https"
              "Connection"        "keep-alive"}}))
