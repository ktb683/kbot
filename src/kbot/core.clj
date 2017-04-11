(ns kbot.core
  (:require
    [manifold.deferred :as d]
    [aleph.http :as http]
    [cheshire.core :as json]
    [clojure.string :as s]
    [endophile.core :refer [mp to-clj html-string]]
    [endophile.hiccup :refer [to-hiccup]]
    [hiccup.core :refer [html]]
    [cprop.core :refer [load-config]]
    [byte-streams :as bs]
    [clojure.pprint :refer [pprint]])
  (:gen-class))

(def config (load-config))
(defonce monitors (read-string (slurp "hats.edn")))

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
  (->> monitors
       find-kbot
       (map find-code-block)))
