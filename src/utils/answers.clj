(ns utils.answers
  (:require
    [monger.json]
    [cheshire.core :refer :all]))

(defn ok
  "Function for get success answer
  :data data for send in answer
  :meta meta information for send in meta"
  [data meta]
  {:body (generate-string {:data data :meta meta :status "Success"})
   :status 200
   :headers {"Content-Type" "application/json"}})
