(ns interceptors.users.attach-user
  (:require [utils.constants :refer :all]
            [utils.helpers :refer :all]
            [services.users-service :as us]
            [utils.jwt :as jwt])
  (:import (org.bson.types ObjectId)))

(defn- find-user
  "Private function for get user from request params & token"
  [connection user-id decoded-id is-owner]
  (if (true? is-owner)
    (us/get-user connection user-id)
    (us/get-user connection user-id decoded-id is-owner)))

;; Interceptor for attach user data
(def attach-user-data
  {:name ::attach-user-data
   :enter
         (fn [context]
           (let [request (get context :request nil)
                 headers (get request :headers nil)
                 path-params (get request :path-params nil)
                 token (get headers auth-header nil)
                 user-id-param (get path-params :user-id nil)
                 decoded-id (if (nil? token)
                              nil
                              (jwt/decode-and-get token :id))]
             (assoc context :user-id (ObjectId. ^String user-id-param)
                            :token token
                            :decoded-id (if (nil? decoded-id) nil (ObjectId. ^String decoded-id))
                            :is-owner (if (nil? decoded-id)
                                        false
                                        (= decoded-id user-id-param)))))})
;; Interceptor for attach user
(def attach-user
  {:name ::attach-user
   :enter
   (fn [context]
     (let [{connection :connection
            user-id :user-id
            decoded-id :decoded-id
            is-owner :is-owner} context
           {document :document
            decoded-user :decoded-user} (find-user connection user-id decoded-id is-owner)]
       (assoc context
         :user document
         :decoded-user decoded-user)))})
