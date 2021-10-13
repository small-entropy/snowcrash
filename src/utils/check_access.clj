(ns utils.check-access
  (:require
    [database.single.users-repository :as rep]
    [utils.rights :as r]
    [utils.constants :refer :all]))

(defn get-fields-by-rule
  "Get list of fields by rule and owner"
  [rule owner public-fields private-fields global-fields]
    (if (or (nil? rule) (nil? owner))
      public-fields
      (if (= owner :my)
        (cond
          (true? (get rule my-global false)) global-fields
          (true? (get rule my-private false)) private-fields
          :else public-fields)
        (cond
          (true? (get rule other-global false)) global-fields
          (true? (get rule other-private false)) private-fields
          :else public-fields))))

(defn check-access
  "Function for get user right"
  ([connection collection-name user-id action-key rule-key]
   (check-access connection collection-name user-id action-key rule-key false))
  ([connection collection-name user-id action-key rule-key default-value]
   (let [user (rep/find-user-by-id connection user-id ["rights"])
         rule (r/get-user-rule user collection-name action-key)]
     (get rule rule-key default-value))))

(defn my-global?
  "Function for check my global access"
  ([connection collection-name user-id action-key]
   (check-access connection collection-name user-id action-key my-global))
  ([connection collection-name user-id action-key default-value]
   (check-access connection collection-name user-id action-key my-global default-value)))

(defn other-global?
  "Function for check global access"
  ([connection collection-name user-id action-key]
   (check-access connection collection-name user-id action-key other-global))
  ([connection collection-name user-id action-key default-value]
   (check-access connection collection-name user-id action-key other-global default-value)))

(defn my-private?
  "Function for check my private access"
  ([connection collection-name user-id action-key]
   (check-access connection collection-name user-id action-key my-private))
  ([connection collection-name user-id action-key default-value]
   (check-access connection collection-name user-id action-key my-private default-value)))

(defn other-private?
  "Function for check private access"
  ([connection collection-name user-id action-key]
   (check-access connection collection-name user-id action-key other-private))
  ([connection collection-name user-id action-key default-value]
   (check-access connection collection-name user-id action-key other-private default-value)))