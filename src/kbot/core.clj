(ns kbot.core
  (:require
   [manifold.deferred :as d]
   [aleph.http :as http]
   [cprop.core :refer [load-config]]
   [byte-streams :as bs])
  (:gen-class))

(def config (load-config))

(defn send-test-request
  "Blocking test get example."
  []
  (-> @(http/get (:api-root config)
                 {:throw-exceptions false
                  :as :json})
      :body
      bs/to-string
      prn))

(defn -main
  "I don't do a whole lot ... yet."
  [& args])
