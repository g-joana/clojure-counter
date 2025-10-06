(ns backend.core
  (:require [org.httpkit.server :as hk]
            [compojure.core :refer [GET defroutes]]
            [compojure.route :as route]
            [cheshire.core :as json]
            [backend.db :as db]
            [backend.ws :as ws])
  (:gen-class))


(def headers
  {"Content-Type" "application/json"
   "Access-Control-Allow-Origin" "*"
   "Access-Control-Allow-Headers" "Content-type, Authorization"
   "Access-Control-Allow-Methods" "PUT, GET, OPTIONS"
   "Access-Control-Allow-Credentials" "true"})

;; handlers
(defn ws-handler [req]
  (try
    (if-not (:websocket? req)
      {:status 200
       :headers {"content-type" "text/html"}
       :body "no websockets connected"}
      (hk/as-channel req
                     {:on-open    ws/on-open
                      :on-receive ws/on-receive
                      :on-close   ws/on-close}))
    (catch Exception e
      (println (str "Internal error: websocket connection: " (.getMessage e)))
      {:status 500
       :headers headers
       :body (json/encode {:error "Internal error: websocket connection"})})))

(defn home-handler []
  (try
    {:status 200
     :headers headers
     :body (json/encode {:counter (db/current-value)})}
    (catch Exception e
      (println (str "Internal error: couldn't get counter value: " (.getMessage e)))
      {:status 500
       :headers headers
       :body (json/encode {:error "Internal error: couldn't get counter value"
                           :details (.getMessage e)})})))

(defroutes app
  (GET "/ws" [] ws-handler)
  (GET "/" [] (home-handler))
  (route/not-found "Not found"))

(defn start-server []
  (println "starting server on port 8080")
  (hk/run-server app {:port 8080}))

(defn -main []
  (start-server))