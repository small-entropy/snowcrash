(ns services.users-service
  (:require
    [utils.password :as pwd]
    [database.single.users-repository :as repository]
    [utils.jwt :as jwt])
  (:import (java.util Date)))

(defn register-user
  "Function for register new user"
  [connection login password]
  (if (or (nil? login) (nil? password))
    (throw (Exception. "Not send data for register user"))
    (let [derived-password (pwd/derive-password password)
          document (repository/create-user connection {:login      login
                                                       :password   derived-password
                                                       :status     "active"
                                                       :created_at (Date.)})
          user {:_id (get document :_id nil)
                :login (get document :login nil)
                :created_at (get document :created_at nil)}
          token (jwt/encode (get document :_id nil) (get document :login))]
      {:document user :token token })))

(defn login-user
  "Function for login user"
  []
  nil)

(defn logout-user
  "Function for logout user"
  []
  nil)

(defn change-user-password
  "Function for change user password"
  []
  nil)

(defn get-users-list
  "Function for get users list"
  []
  nil)

(defn get-user
  "Function for get user"
  []
  nil)