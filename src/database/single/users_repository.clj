(ns database.single.users-repository
  (:require
    [database.common.repository :as repository]))

(defonce collection "users")

(defn create-user
  "Function for create user in database"
  [connection data]
  (repository/create-document connection collection data))