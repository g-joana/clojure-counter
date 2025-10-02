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


(def channels (atom #{}))

(defn on-open    [ch]
  (swap! channels conj ch)
  (println (str  "client connected: " ch)))
(defn on-close   [ch status-code]
  (swap! channels disj ch)
  (println (str  "channel disconnected: " ch " status: " status-code)))
(defn on-receive [ch message]
  (doseq [client @channels]
    (hk/send! client (str ch " broadcasting: " message))))



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

(defn inc-handler []
  {:status 200
   :headers headers
   :body (json/encode {:counter (db/inc-counter!)})})

(defn reset-handler []
  {:status 200
   :headers headers
   :body (json/encode {:counter (db/reset-counter!)})})

(defn options-handler []
  {:status 200
   :headers headers
   :body ""})


(defroutes app
  (GET "/ws" [] ws-handler)
  (GET "/" [] (home-handler))
  (PUT "/inc" [] (inc-handler))
  (PUT "/reset"  [] (reset-handler))
  (OPTIONS "/inc" [] (options-handler))
  (OPTIONS "/reset" [] (options-handler))
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
