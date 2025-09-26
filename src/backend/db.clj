(ns backend.db
  (:require [datomic.client.api :as d])
  (:require [clojure.pprint :as pp]))


;; config

(def client (d/client {:server-type   :datomic-local
                       :system "counter"
                       ;; :storage-dir dev-local stores databases under $ {storage-dir} /$ {system} /$ {db-name}
                       }))

(def schema [{:db/ident :counter/value
              :db/valueType :db.type/long
              :db/cardinality :db.cardinality/one}])

(defn create-connection []
  (d/create-database client {:db-name "counter-state"})
  (d/connect client {:db-name "counter-state"}))

(defn create-schema [conn]
  (d/transact conn {:tx-data schema}))

(defn delete-db []
  (d/delete-database client {:db-name " counter-state "}))

;; db operations

;; not working
(defn upsert! [conn new-value]
  (let [db (d/db conn)
        eid (ffirst (d/q '[:find ?e :where [?e :counter/value]] db))]
    (if eid
      (d/transact conn [{:db/id eid :counter/value new-value}])
      (d/transact conn [{:counter/value new-value}]))))


(defn current-value [conn]
  (let [snapshot (d/db conn)]
    (ffirst (d/q '[:find ?v
                   :where [_ :counter/value ?v]]
                 snapshot))))

;; print db
(defn list-facts [conn]
  (let [db (d/db conn)]
    (d/datoms db {:index :eavt})))

(defn eids [conn]
  (d/q '[:find ?e
         :where [?e :counter/value]]
       (d/db conn)))

;; test

(def conn (create-connection))
(create-schema conn)
(upsert! conn 0)
(current-value conn)
(list-facts conn)
(eids conn)


; #datom[entity-id attribute-id value tx-id added?]