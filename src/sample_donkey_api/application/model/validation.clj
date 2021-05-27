(ns sample-donkey-api.application.model.validation
  (:import (org.apache.commons.validator.routines InetAddressValidator)))

(def ^:private ^InetAddressValidator inet-address-validator (InetAddressValidator/getInstance))

(defn- only-valid-characters?
  [^String to-check is-valid-fn]
  (when-let [length (some-> to-check .length)]
    (loop [i 0]
      (if (= i length)
        true
        (if (is-valid-fn (.charAt to-check i))
          (recur (inc i))
          false)))))

(defn alphanumeric? [to-check]
  (only-valid-characters? to-check #(Character/isLetterOrDigit ^Character %)))

(defn valid-ip? [^String ip]
  (.isValid inet-address-validator ip))
