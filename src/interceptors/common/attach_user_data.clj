(ns interceptors.common.attach-user-data
  (:require
    [utils.jwt :as jwt]
    [utils.constants :refer :all])
  (:import (org.bson.types ObjectId)))

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