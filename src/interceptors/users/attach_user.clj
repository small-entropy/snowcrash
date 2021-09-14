(ns interceptors.users.attach-user
  (:require [utils.constants :refer :all]
            [utils.helpers :refer :all]
            [services.users-service :as us])
  (:import (org.bson.types ObjectId)))

(defn- find-user
  "Private function for get user from request params & token"
  [connection user-id decoded-id is-owner]
  (if (true? is-owner)
    (us/get-user connection (ObjectId. ^String user-id))
    (let [user-oid (ObjectId. ^String user-id)
          decoded-oid (ObjectId. ^String decoded-id)]
      (us/get-user connection user-oid decoded-oid is-owner))))

(def attach-user
  {:name ::attach-user
   :enter
   (fn [context]
     (let [{connection :connection
            user-id :user-id
            decoded-id :decoded-id
            is-owner :is-owner} (get context :request nil)
           {document :document
            decoded-user :decoded-user} (find-user connection user-id decoded-id is-owner)]
       (assoc context
         :user document
         :decoded-user decoded-user)))})
