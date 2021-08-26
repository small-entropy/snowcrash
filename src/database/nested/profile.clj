(ns database.nested.profile
  (:import (java.util Date)
           (org.bson.types ObjectId)))

(defn get-default-user-profile
  "Function for get default user profile"
  []
  [
   {:_id (ObjectId.) :key "registered" :value (Date.)}
   {:_id (ObjectId.) :key "avatar" :value nil}
   {:_id (ObjectId.) :key "age" :value nil}
   {:_id (ObjectId.) :key "sex" :value nil}
   {:_id (ObjectId.) :key "country" :value nil}
   {:_id (ObjectId.) :key "city" :value nil}
   {:_id (ObjectId.) :key "language" :value nil}
   ])
