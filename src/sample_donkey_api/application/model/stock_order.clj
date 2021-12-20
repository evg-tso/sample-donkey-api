(ns sample-donkey-api.application.model.stock-order
  (:require
    [integrant.core :as ig]
    [malli.core :as malli]
    [sample-donkey-api.application.model.validation :as validation])
  (:import
    (java.math
      BigDecimal)
    (org.apache.commons.validator.routines
      InetAddressValidator)))

(def ^:private ^InetAddressValidator inet-address-validator (InetAddressValidator/getInstance))

(def ^:private NonEmptyString
  (malli/schema
    [:string {:min 1}]))

(def ^:private StockID
  (malli/schema
    [:and
     {:description         "This is the stock ID"
      :json-schema/example "AAPL"}
     NonEmptyString
     [:fn {:error/message "should be alphanumeric"} validation/alphanumeric?]]))

(defn- valid-big-decimal? [x]
  (instance? BigDecimal x))

(def ^:private PreciseFloatingNumber
  (malli/-simple-schema
    {:type            :preload/big-decimal
     :pred            valid-big-decimal?
     :type-properties {:json-schema/type   "number"
                       :json-schema/format "double"}}))

(def ^:private AmountUSD
  (malli/schema
    [:and
     {:description         "The amount to buy/sell"
      :json-schema/example 3.47}
     PreciseFloatingNumber
     [:not= 0]]))

(def ^:private RequestID
  (malli/schema
    [:uuid
     {:description "A unique identifier for the request, used for tracked purposes"
      :optional    true}]))

(defn- valid-ip? [^String ip]
  (and (string? ip)
       (.isValid inet-address-validator ip)))

(def ^:private IP
  (malli/-simple-schema
    {:type            :sample/ip
     :pred            valid-ip?
     :type-properties {:error/message       "should be a valid IPv4 or a valid IPv6 address"
                       :json-schema/example "35.244.183.10"
                       :description         "The device IP Address"}}))

(def ^:private Direction
  (malli/schema
    [:enum
     {:json-schema/example "buy"
      :json-schema/type    "string"
      :description         "The direction of the operation, buy or sell"}
     "buy" "sell"]))

(def path
  (malli/schema
    [:map [:stock-id StockID]]))

(def body
  (malli/schema
    [:map
     [:amount_usd AmountUSD]
     [:request_id {:optional true} RequestID]
     [:ip IP]
     [:direction Direction]]))

(def request
  (malli/schema
    [:map
     [:path-params path]
     [:body-params body]]))

(defmethod ig/init-key :model/create-stock-order [_ _]
  request)
