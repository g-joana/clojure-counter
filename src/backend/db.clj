(ns backend.db
  (:require [datomic.client.api :as d])
  (:require [clojure.pprint :as pp]))

(def client (d/client {:server-type   :datomic-local
                       :system "counter"
                       ;; :storage-dir dev-local stores databases under $ {storage-dir} /$ {system} /$ {db-name}
                       }))

(def schema [{:db/ident :counter/value  ;; entity is represented by entity/attribute 
              :db/valueType :db.type/long
              :db/cardinality :db.cardinality/one}])

(defn create-connection []
  (d/create-database client {:db-name "counter-state"})
  (d/connect client {:db-name "counter-state"}))

;; (def conn (create-connection))

(defn create-schema [conn]
  (d/transact conn {:tx-data schema}))
;; transact returns future(a kind of promise)

(defn update! [conn new-value]
  (d/transact conn [{:db/id [:db/ident :counter/value]
                      :counter/value new-value}]))


;; (d/list-databases client {})

(def new-value {:counter/value 0})
;; (d/transact conn {:tx-data [new-value]})



;; (defn snapshot (d/db conn))

(defn current-value [conn]
  (let [snapshot (d/db conn)]
    (d/q '[:find ?counter
           :where [_ :counter/value ?counter]]
         snapshot)))


(defn init-counter! [conn]
  (when-not (current-value conn)
    (d/transact conn
                 [{:db/ident :counter/value
                   :counter/value 0}])))


(defn safe-increment! [conn]
  (let [snapshot (d/db conn)
        eid [:db/ident :counter/value]
        current (:counter/value (d/pull snapshot [:counter/value] eid))]
    (println (str "try" current " -> " (inc current)))
    (d/transact conn
                 [{:db/ident :counter/value
                   :counter/value [:db.fn/cas :counter/value current (inc current)]}])))
;; if not, try again?

;; (defn inc-db! [conn]
;; snapshot
;; get datom current value
;; increment value
;; update-db!
;; )


;; [:db/add entity-id attribute value]
;; [:db/retract entity-id attribute value?]

;; (first (last current-value))

;; (seq (d/datoms db {:index :eavt}))




;; #datom[entity-id attribute-id value tx-id added?]

(defn delete-db []
  (d/delete-database client {:db-name "counter-state"}))

;; (delete-db)
