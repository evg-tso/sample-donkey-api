(ns sample-donkey-api.utils.json
  (:require [jsonista.core :as json]))

(def ^:private json-mapper
  (json/object-mapper {:decode-key-fn    true
                       :encode-key-fn    true
                       :escape-non-ascii true}))

(defn parse
  ([val]
   (parse val json-mapper))
  ([val mapper]
   (json/read-value val mapper)))

(defn stringify
  ([val]
   (stringify val json-mapper))
  ([val mapper]
   (json/write-value-as-string val mapper)))
