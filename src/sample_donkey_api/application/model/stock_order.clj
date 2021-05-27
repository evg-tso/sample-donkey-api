(ns sample-donkey-api.application.model.stock-order
  (:require [sample-donkey-api.application.model.validation :as validation]
            [integrant.core :as ig]))

(def ^:private non-empty-string [:string {:min 1}])

(def ^:private stock-id [:and
                         {:description "This is the stock ID"}
                         non-empty-string
                         [:fn {:error/message "should be alphanumeric"} validation/alphanumeric?]])

(def ^:private amount-usd [:and
                           {:description         "The amount to buy/sell"
                            :json-schema/example "3.47"}
                           :double
                           [:not= 0]])

(def ^:private request-id [:and
                           {:description         "A unique identifier for the request, used for tracked purposes"
                            :json-schema/example "71dad7da-7926-40d8-9b15-b94a6d46e15a"
                            :optional            true}
                           :uuid])

(def ^:private ip [:and
                   {:json-schema/example "35.244.183.10"
                    :description         "The Device’s IP Address"}
                   [:string {:min 1 :max 46}]
                   [:fn {:error/message "should be a valid IPv4 or a valid IPv6 address"} validation/valid-ip?]])

(def ^:private direction [:and
                          {:json-schema/example "buy"
                           :json-schema/type    "string"
                           :description         "The direction of the operation, buy or sell"}
                          [:enum "buy" "sell"]])

(def path [:map [:stock_id stock-id]])

(def body [:map
           [:amount_usd amount-usd]
           [:request_id {:optional true} request-id]
           [:ip ip]
           [:direction direction]])

(def request [:map
              [:path-params path]
              [:body-params body]])

(defmethod ig/init-key :model/create-stock-order [_ _]
  request)