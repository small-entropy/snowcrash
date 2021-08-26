(ns database.single.users-repository
  (:require
    [database.common.repository :as repository]))

(defonce collection "users")

(defn create-user
  "Function for create user in database"
  [connection data]
  (repository/create-document connection collection data))

(defn find-user-by-username
  [connection login]
  (let [user (first (repository/get-list-by-filter
                      connection
                      collection
                      {:login login}
                      []))]
    (if (nil? user)
      (throw (Exception. "Can not find user by login"))
      user)))