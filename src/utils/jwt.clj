(ns utils.jwt
  (:import org.bson.types.ObjectId)
  (:require
    [buddy.core.hash :as hash]
    [buddy.sign.jwt :as jwt]))

;; Define salt value
(defonce salt (hash/sha512 "super#@!$ecretSa|t"))

(defn encode
  "Function for create JSON Web Token"
  [id login]
  (jwt/sign {:id (if (string? id) id (str id)) :login login} salt))

(defn decode
  "Function for decode JSON Web Token"
  [token]
  (jwt/decrypt token salt))