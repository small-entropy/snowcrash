(ns utils.rights
  (:require [clojure.string :as s]
            [utils.constants :refer :all]))

(defn get-right-as-map
  "Function for get right as map (hash-map)"
  [right-str]
  (if (nil? right-str)
    (throw (Exception. "Not send right string"))
    (let [right-str-val (map
                          (fn
                            [val]
                            (if (= val "1") true false)) (s/split right-str #""))
          right-keys [my-global
                      my-private
                      my-public
                      other-global
                      other-private
                      other-public]]
      (zipmap right-keys right-str-val))))

(defn check-rule-in-right-string
  "Function for check rule from right string"
  [right-str rule-key]
  (get (get-right-as-map right-str) rule-key false))

(defn check-my-global
  "Function for check my global rule from right string"
  [right-str]
  (check-rule-in-right-string right-str my-global))

(defn check-my-private
  "Function for check my private rule from right string"
  [right-str]
  (check-rule-in-right-string right-str my-private))

(defn check-my-public
  "Function for check my public rule from right string"
  [right-str]
  (check-rule-in-right-string right-str my-public))

(defn check-other-global
  "Function for check other global rule from right string"
  [right-str]
  (check-rule-in-right-string right-str other-global))

(defn check-other-private
  "Function for check other private rule from right string"
  [right-str]
  (check-rule-in-right-string right-str other-private))

(defn check-other-public
  "Function for check other public rule from right string"
  [right-str]
  (check-rule-in-right-string right-str other-public))

(defn get-user-rule
  "Function for check access user to action"
  [user right-name action-name]
  (let [user-rights (get user :rights nil)]
    (if (nil? user-rights)
      (throw (Exception. "Can not get list of user rights"))
      (let [founded-right (first (filter #(= (:name %) right-name) user-rights))]
        (if (nil? founded-right)
          (throw (Exception. "Can not get user right"))
          (get-right-as-map (get founded-right action-name nil)))))))

(defn check-access
  "Function for check access to action by leve"
  [user right-name action-name access-level]
  (let [rule (get-user-rule user right-name action-name)]
    (get rule access-level false)))