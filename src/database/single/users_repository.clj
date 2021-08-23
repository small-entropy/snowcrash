(ns database.single.users-repository
  (:require [database.common.repository :as repository]))

(defonce collection "users")

(defn create-user
  "Function for create user in database"
  [database data]
  (repository/create-document database collection data))