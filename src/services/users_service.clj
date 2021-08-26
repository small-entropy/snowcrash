(ns services.users-service
  (:require
    [utils.password :as pwd]
    [database.single.users-repository :as rep]
    [database.nested.profile :as prof]
    [database.nested.property :as prop]
    [database.nested.right :as right]
    [utils.jwt :as jwt])
  (:import (org.bson.types ObjectId)))

(defn register-user
  "Function for register new user"
  [connection login password]
  (if (or (nil? login) (nil? password))
    (throw (Exception. "Not send data for register user"))
    (let [derived-password (pwd/derive-password password)
          oid (ObjectId.)
          token (jwt/encode oid login)
          document (rep/create-user connection {:_id oid
                                                       :login      login
                                                       :password   derived-password
                                                       :status     "active"
                                                       :profile    (prof/get-default-user-profile)
                                                       :properties (prop/get-default-user-properties)
                                                       :rights     (right/get-default-user-right)
                                                       :tokens     [token]})
          user {:_id (get document :_id nil)
                :login (get document :login nil)
                :profile (get document :profile nil)}]
      {:document user :token token })))

(defn login-user
  "Function for login user"
  [connection login incoming-password]
  (if (or (nil? login) (nil? incoming-password))
    (throw (Exception. "Not send login or password"))
    (let [founded-user (rep/find-user-by-username connection login)
          derived-password (get founded-user :password nil)
          result-check (pwd/check-password incoming-password derived-password)
          user (if result-check
                 {:_id (get founded-user :_id nil)
                  :login login
                  :profile (get founded-user :profile nil)}
                 nil)]
      (if (nil? user)
        (throw (Exception. "Not correct user password"))
        {:document user :token (first (get founded-user :tokens nil))}))))

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