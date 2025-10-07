(ns frontend.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))


(defn update-counter! [new-value]
  (when-let [element (.getElementById js/document "counter")]
    (set! (.-innerHTML element) new-value)))

(defn fetch []
  (go (let [response (<! (http/get "http://127.0.0.1:8080/"
                                   {:with-credentials? false
                                    :headers {"accept" "application/json"}}))]
        (update-counter! (get (:body response) :counter)))))
(fetch)

(defn reset []
  (go (let [response (<! (http/get "http://127.0.0.1:8080/reset"
                                   {:with-credentials? false
                                    :headers {"accept" "application/json"}}))]
        (update-counter! (get (:body response) :counter)))))

(defn increment []
  (go (let [response (<! (http/get "http://127.0.0.1:8080/inc"
                                   {:with-credentials? false
                                    :headers {"accept" "application/json"}}))]
        (update-counter! (get (:body response) :counter)))))

(defn setup-buttons []
  (when-let [button (.getElementById js/document "increment")]
    (.addEventListener button "click" increment))
  (when-let [button (.getElementById js/document "reset")]
    (.addEventListener button "click" reset)))

(setup-buttons)