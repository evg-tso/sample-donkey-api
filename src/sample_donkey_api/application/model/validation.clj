(ns sample-donkey-api.application.model.validation)

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
