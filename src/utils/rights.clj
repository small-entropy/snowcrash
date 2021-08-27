(ns utils.rights
  (:require [clojure.string :as s]))

(def my-global :my-global)
(def my-private :my-private)
(def my-public :my-public)
(def other-global :other-global)
(def other-private :other-private)
(def other-public :other-public)

(defn get-right-as-map
  "Function for get right as map (hash-map)"
  [right-str]
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
    (zipmap right-keys right-str-val)))

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