(ns interceptors.users.users-interceptors
  (:require
    [services.users-service :as service]
    [utils.answers :refer :all]
    [utils.helpers :refer :all]
    [utils.constants :refer :all]))

;; Interceptor for get paginate list users list
(def list-users-interceptor
  {:name ::list-users-interceptor
   :enter
    (fn [context]
      (let [{request :request guid :guid connection :connection} context
            query-params (get request :query-params nil)
            limit (if (nil? query-params) 10 (Integer/parseInt (get query-params :limit "10")))
            skip (if (nil? query-params) 0 (Integer/parseInt (get query-params :skip "0")))
            {header-token auth-header} (get request :headers nil)
            {documents :documents token :token total :total} (service/get-users-list connection header-token limit skip)]
        (assoc context :response (ok guid documents {:token token
                                                     :request guid
                                                     :total total
                                                     :limit limit
                                                     :skip skip}))))})
;; Interceptor for get user document
(def entity-users-interceptor
  {:name ::entity-users-interceptor
   :enter
     (fn [context]
       (let [{is-owner :is-owner
              user-id :user-id
              guid :guid
              token :token
              connection :connection
              decoded-id :decoded-id} context
             {document :document} (if (nil? token)
                                    (service/get-user connection user-id)
                                    (service/get-user connection user-id decoded-id is-owner))]
         (assoc context :response (ok guid document {:token (not-send token)
                                                     :request guid}))))})