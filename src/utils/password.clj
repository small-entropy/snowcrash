(ns utils.password
  (:require [buddy.hashers :as hashers]))

(defonce trusted-algs #{ :bcrypt+blake2b-512 })

(defn derive-password
  "Function for encrypt password"
  [password]
  (hashers/derive password {:alg :bcrypt+blake2b-512
                            :iterations 12}))

(defn check-password
  "Function for check password hash"
  [incoming-pwd derived-pwd]
  (hashers/check incoming-pwd derived-pwd {:limit trusted-algs }))