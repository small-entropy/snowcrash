(ns interceptors.books.check-access
  (:require
    [utils.jwt :as jwt]
    [utils.rights :as rght]
    [database.single.users-repository :as r]
    [utils.constants :refer :all])
  (:import (org.bson.types ObjectId)))

;; Interceptor check access to create, update & delete for books actions.
;; Add to context document-id from request params.
;; Add to context decoded-id (user id from token).
;; Add to context token.
;; If can't get user, user id, access to action - throw errors.
(defn books-access
  [collection]
  {:name ::check-user-access
   :enter
         (fn [context]
           (let [connection (get context :connection nil)
                 request (get context :request nil)
                 headers (get request :headers nil)
                 path-params (get request :path-params)
                 token (get headers auth-header nil)
                 document-id (get path-params :document-id nil)
                 decoded-id (if (nil? token)
                              (throw (ex-info
                                       "Token not send"
                                       {:alias "not-send-token"
                                        :info {:headers headers
                                               :_id document-id}}))
                              (jwt/decode-and-get token :id))
                 user (r/find-user-by-id connection decoded-id ["_id" "login" "rights"])
                 action-key (case (get request :request-method :post)
                              :post :create
                              :put :update
                              :delete :delete)
                 rule (rght/get-user-rule user collection action-key)]
             (if (get rule other-global false)
               (assoc context
                 :document-id (if (nil? document-id) nil (ObjectId. ^String document-id))
                 :token token
                 :user {:_id (get user :_id nil)
                        :login (get user :login nil)})
                 (throw (ex-info
                          "Has not access to action"
                          {:alias "has-not-access"
                           :info {:_id document-id
                                  :user (get user :_id nil)
                                  :token token}})))))})
