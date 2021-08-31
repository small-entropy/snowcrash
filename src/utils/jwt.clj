(ns utils.jwt
  (:import org.bson.types.ObjectId)
  (:require
    [buddy.core.hash :as hash]
    [buddy.sign.jwt :as jwt]
    [clj-time.core :as time]))

;; Define salt value
(defonce salt (hash/sha256 "super#@!$ecretSa|t"))

(defn encode
  "Function for create JSON Web Token"
  [id login]
  (let [claims {:id    (if (string? id) id (str id))
                :login login
                :exp   (time/plus (time/now) (time/days 30))}
        opts {:alg :dir :enc :a128cbc-hs256}]
    (jwt/encrypt claims salt opts)))

(defn decode
  "Function for decode JSON Web Token"
  [token]
  (jwt/decrypt token salt))

(defn decode-and-get
  "Function for get field from claims"
  [token key]
  (let [decoded (decode token)]
    (get decoded key nil)))