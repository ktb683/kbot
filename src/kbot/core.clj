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
   [endophile.core :refer [mp to-clj html-string]]
   [endophile.hiccup :refer [to-hiccup]]
   [hiccup.core :refer [html]]
   [cprop.core :refer [load-config]]
   [byte-streams :as bs]
   [me.raynes.conch :refer [programs with-programs let-programs] :as sh]
   [clojure.pprint :refer [pprint]])
  (:gen-class))

(def config (load-config))

;;(defonce monitors (read-string (slurp "hats.edn")))

(defrecord Notification [id title subtitle message actions])

(defn terminal-notifier [^Notification n]
  (future
    (with-programs [terminal-notifier]
      (terminal-notifier
       "-json"
       "-title" (:title n)
       "-subtitle" (:subtitle n)
       "-message" (:message n)
       "-closeLabel" "No"
       "-actions" (apply str (interpose "," (:actions n)))))))

(comment
  (let [current-notification (Notification. nil "KBOT" "alert -XYZ" "To scale?" #{"Go", "Mo"})
        res (json/decode @(terminal-notifier current-notification) true)]
    ((:actions current-notification) (:activationValue res))))

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

(defn make-hats []
  (spit "hats.edn" (into [] (api-get "monitor" {}))))

(defn find-kbot [m]
  (filter (fn [mon] (and (= "Alert" (:overall_state mon)) (s/includes? (:message mon) "@kbot"))) m))

(defn find-code-block [monitor]
  (->>
   (to-hiccup (mp (:message monitor)))
   (filterv (fn [x] (= :code (first (second x)))))
   flatten
   (filterv string?)
   (assoc monitor :command)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]

  #_(->> monitors
         find-kbot
         (map find-code-block)))
