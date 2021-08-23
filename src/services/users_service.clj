(ns services.users-service
  (:require
    [utils.password :as pwd]
    [database.single.users-repository :as repository]
    [utils.jwt :as jwt]
    [monger.json :as json]))

(defn register-user
  "Function for register new user"
  [database login password]
  (if (or (nil? login) (nil? password))
    (throw (Exception. "Not send data for register user"))
    (let [derived-password (pwd/derive-password password)
          document (repository/create-user database {:login login
                                                     :password derived-password})
          token (jwt/encode (get document :_id nil) (get document :login))]
      {:document document :token token })))

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