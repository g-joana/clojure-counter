(ns backend.core
  (:require [org.httpkit.server :as hk]
            [compojure.core :refer [GET PUT OPTIONS defroutes]]
            [compojure.route :as route]
            [cheshire.core :as json]
            [backend.db :as db]
            [clojure.pprint :as pp]))


(def headers
  {"Content-Type" "application/json" "Access-Control-Allow-Origin" "*"
   "Access-Control-Allow-Headers" "Content-type, Authorization"
   "Access-Control-Allow-Methods" "PUT, GET, OPTIONS" "Access-Control-Allow-Credentials" "true"})

;; websocket req
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

;; handlers
(defn ws-handler [req]
  (if-not (:websocket? req)
    {:status 200
     :headers {"content-type" "text/html"}
     :body "no websockets connected"}
    (hk/as-channel req
                   {:on-open    on-open
                    :on-receive on-receive
                    :on-close   on-close})))

(defn home-handler []
  {:status 200
   :headers headers
   :body (json/encode {:counter (db/current-value)})})


(defroutes app
  (GET "/ws" [] ws-handler)
  (GET "/" [] (home-handler))
  (route/not-found "Not found"))

(defn start-server []
  (println "starting server on port 8080")
  ;; graceful shutdown: wait 100ms for existing requests to be finished
  ;; :timeout is optional, when no timeout, stop immediately
  (hk/run-server app {:port 8080}))

(defn -main []
  (start-server))

;; interactive dev
(defonce server (atom nil))

(defn start []
  (reset! server (start-server)))

(defn stop []
  (when @server
    (@server :timeout 100)
    (reset! server nil)
    (println "Server after reset:" @server)))

(defn restart []
  (stop)
  (start))