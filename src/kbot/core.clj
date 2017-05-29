(ns kbot.core
  (:require
    [manifold.deferred :as d]
    [aleph.http :as http]
    [cheshire.core :as json]
    [clojure.string :as s]
    [clojure.core.async
     :as a
     :refer [>! <! >!! <!! go go-loop chan buffer close! thread
             alts! alts!! timeout]]
    ;;[endophile.core :refe r [mp to-clj html-string]]
    ;;[endophile.hiccup :refer [to-hiccup]]
    ;;[me.raynes.cegdown :as md]
    [hiccup.core :refer [html]]
    [cprop.core :refer [load-config]]
    [byte-streams :as bs]
    [me.raynes.conch :refer [programs with-programs let-programs] :as sh]
    [clojure.pprint :refer [pprint]])
  (:gen-class))

(def config (load-config))

;; dummy data loaded in
(def monitors (read-string (slurp "hats.edn")))

(defrecord Notification [id title subtitle message actions])

;; {:id "" ... }



(defn api-get
  "Blocking test get example."
  [endpoint params]
  (-> @(http/get (str (:api-root config)
                      "v1/"
                      endpoint
                      "?api_key="
                      (:api-key config)
                      "&application_key="
                      (:app-key config))
                 {:as :json})
      :body))

;; make dummy data file
(defn make-hats []
  (spit "hats.edn" (into [] (api-get "monitor" {}))))

(defn find-first-kbot [m]
  (first (filter (fn [mon] (and (= "Alert" (:overall_state mon))
                                (s/includes? (:message mon) "@kbot")))
                 m)))

(defn find-code-block
  "finds the ``` within the :message and grabs what's b/w it"
  [monitor]
  (->> monitor
      :message
      (re-find #"```(.*)```")
      second
  ))

(defn terminal-notifier [^Notification n]
  ;; future is a promise of giving a value. an IOU if you will.
  (future
    (with-programs [terminal-notifier]
                   (terminal-notifier
                     "-json"
                     "-title" (:title n)
                     "-subtitle" (:subtitle n)
                     "-message" (:message n)
                     "-closeLabel" "No"
                     "-actions" (apply str (interpose "," (:actions n)))))))

(defn test-notification []
  (let [current-notification (Notification. nil "KBOT" "alert -XYZ" "To scale?" #{"Go", "Hi", "Op"})
        res (json/decode @(terminal-notifier current-notification) true)]
    ((:actions current-notification) (:activationValue res))))

(defn create-notification-from-alert
  "builds notification from alert and gives a future (promise/IOU)"
  [alerted-monitor]

  ;;TODO take alerted thing to do shit
  (let [title "meow"
        subtitle "cats"
        message "beep"
        actions #{"1", "2", "3"}
        notification (Notification. nil title subtitle message actions)]
    (terminal-notifier notification)
    ))

(defn put-notification-in-queue
  ""
  [blah]
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;
(comment (defn do-nothing [value]
  (println value)
  "OK")

(defn call-with-value
  "Call a function `to-call` with value from `c1` and return to `c2`"
  [fn-to-call ch1 ch2]
  (go-loop []
    (let [value (<! ch1)]
      (>! ch2 (fn-to-call value))
      (recur))))

(defn pipeline []
  (let [timer-check (chan)
        server-check (chan)
        notify (chan)
        action (chan)]

    ;; put something on timer-check from something else.
    (call-with-value do-nothing timer-check server-check)
    ;;(call-with-value open-notification server-check notify)
    ;;(call-with-value handle-notification-response notify action)
    ;; take from action somewhere.

    ;;(>!! timer-check "Zombies")

    )))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn -main
  "I don't do a whole lot ... yet."
  [& args]

  #_(->> monitors
         find-kbot
         (map find-code-block)))
