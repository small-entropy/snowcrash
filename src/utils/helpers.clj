(ns utils.helpers)

(defn not-send
  [value]
  (if (nil? value) "Not send" value))

