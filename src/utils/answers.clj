(ns utils.answers
  (:require
    [monger.json]
    [cheshire.core :refer :all]))

(defn- get-answer
  "Private function for get answer"
  [data meta code result]
  (let [token (if (nil? meta) nil (get meta :token nil))
        headers (if (nil? token)
                  {"Content-Type" "application/json"}
                  {"Content-Type" "application/json" "Authorization" token})]
    {:body (generate-string {:data data
                             :meta meta
                             :result result})
     :status code
     :headers headers}))

(defn ok
  "Function for get success answer"
  [data meta]
  (get-answer data meta 200 "success"))

(defn created
  "Function for get success access for create operation"
  [data meta]
  (get-answer data meta 201 "created"))
