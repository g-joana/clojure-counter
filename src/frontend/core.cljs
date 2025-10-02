(ns frontend.core
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn update-counter! [new-value]
  (when-let [element (.getElementById js/document "counter")]
    (set! (.-innerHTML element) new-value)))

(defn fetch []
  (-> (js/fetch "http://localhost:8080/"
                (clj->js {:method "GET"
                          :headers {"Accept" "application/json"}}))
      (.then #(.json %))
      (.then #(do
                (update-counter! (.-counter %))))
      (.catch #(js/console.log "Error:" %))))

(fetch)


;; websockets
(defonce ws-connection (atom nil))

(defn on-open [event]
  (println "ws connection established")
  (js/console.log "ws opened:" event))

(defn on-message [event]
  (update-counter! (js/parseInt (.-data event))))

(defn on-error [event]
  (js/console.error "ws error:" event)
  (println "ws error: " event))

(defn on-close [event]
  (println "ws connection closed")
  (js/console.log "code:" (.-code event) "reason:" (.-reason event))
  (reset! ws-connection nil))

(defn connect! [url]
  (if @ws-connection
    (println (str "connected to" url))
    (let [ws (js/WebSocket. url)]
      (set! (.-onopen ws) on-open)
      (set! (.-onmessage ws) on-message)
      (set! (.-onerror ws) on-error)
      (set! (.-onclose ws) on-close)
      (reset! ws-connection ws)
      (println (str "connecting to " url "...")))))

(connect! "http://localhost:8080/ws")



;; could use reagent atoms to update values
(defn setup-buttons []
  (when-let [button (.getElementById js/document "increment")]
    (.addEventListener button "click" #(.send @ws-connection "inc")))
  (when-let [button (.getElementById js/document "reset")]
    (.addEventListener button "click" #(.send @ws-connection "reset"))))

(setup-buttons)



