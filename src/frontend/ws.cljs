(ns frontend.ws)

(defonce connection (atom nil))

(defn update-counter! [new-value]
  (when-let [element (.getElementById js/document "counter")]
    (set! (.-innerHTML element) new-value)))

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
  (reset! connection nil))

(defn connect! [url]
  (if @connection
    (println (str "connected to" url))
    (let [ws (js/WebSocket. url)]
      (set! (.-onopen ws) on-open)
      (set! (.-onmessage ws) on-message)
      (set! (.-onerror ws) on-error)
      (set! (.-onclose ws) on-close)
      (reset! connection ws)
      (println (str "connecting to " url "...")))))

(defn send [msg]
  (when-not @connection
    (connect! "http://localhost:8080/ws"))
  (.send connection msg))

(connect! "http://localhost:8080/ws")