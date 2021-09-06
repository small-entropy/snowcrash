(ns utils.answers
  (:require
    [monger.json]
    [cheshire.core :refer :all]))

(defn- get-headers
  [token guid]
  {"Content-Type" "application/json"
   "Authorization" (if (nil? token) "None" token)
   "X-Request-GUID" (if (nil? guid) "None" (str guid))})

(defn- get-answer
  "Private function for get answer"
  [guid data meta code result]
  (let [token (if (nil? meta) nil (get meta :token nil))
        headers (get-headers token guid)]
    {:body (generate-string {:data data
                             :meta meta
                             :result result})
     :status code
     :headers headers}))

(defn- get-error
  [guid error meta code result]
  (let [headers (get-headers nil guid)]
    {:body (generate-string {:error error
                             :meta meta
                             :result result})
     :status code
     :headers headers}))

(defn ok
  "Function for get success answer"
  [guid data meta]
  (get-answer guid data meta 200 "success"))

(defn created
  "Function for get success access for create operation"
  [guid data meta]
  (get-answer guid data meta 201 "created"))

(defn fail
  ([guid error meta]
   (get-error guid error meta 500 "fail"))
  ([guid error meta code alias]
   (get-error guid error meta code alias)))