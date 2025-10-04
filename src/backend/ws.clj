(ns backend.ws
  (:require [backend.db :as db]
            [org.httpkit.server :as hk]))

(def channels (atom #{}))

(defn on-open    [ch]
  (swap! channels conj ch)
  (println (str  "channel connected: " ch)))

(defn on-close   [ch status-code]
  (swap! channels disj ch)
  (println (str  "channel disconnected: " ch " status: " status-code)))

;; update to safer broadcast
(defn on-receive [ch message]
  (let [new-val (case message
                  "inc" (str (db/inc-counter!))
                  "reset" (str (db/reset-counter!)))]
    (doseq [client @channels]
      (case (not= client ch)
        (hk/send! client new-val)))))