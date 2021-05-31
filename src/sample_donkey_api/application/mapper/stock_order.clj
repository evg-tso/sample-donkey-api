(ns sample-donkey-api.application.mapper.stock-order)

(defn request->map [req]
  {:amount_usd (-> req :body-params :amount_usd)
   :request_id (-> req :body-params :request_id)
   :ip         (or (-> req :sample/resolved-ip) (-> req :body-params :ip))
   :direction  (-> req :body-params :direction)
   :stock_id   (-> req :path-params :stock-id)})
