(ns utils.helpers)

(defn not-send
  "Helper function for check value on exist &
  return value or string (Not send)"
  [value]
  (if (nil? value) "Not send" value))

