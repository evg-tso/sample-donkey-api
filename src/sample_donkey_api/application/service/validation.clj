(ns sample-donkey-api.application.service.validation
  (:require [sample-donkey-api.application.protocols :as protocols]
            [malli.core :as malli]
            [malli.error :as malli-error]
            [integrant.core :as ig]))

(deftype ^:private ValidationService [validator explainer]
  protocols/IValidationService
  (valid? [_ request]
    (validator request))
  (explain [_ request]
    (explainer request)))

(defn- create-malli-validation-service [schema]
  (let [explainer (malli/explainer schema)
        validator (malli/validator schema)]
    (->ValidationService validator
                         #(malli-error/humanize (explainer %)))))

(defmethod ig/init-key :service/validation [_ {:keys [schema]}]
  (create-malli-validation-service schema))
