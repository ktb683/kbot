(ns kbot.core
  (:require
   [manifold.deferred :as d]
   [aleph.http :as http]
   [cheshire.core :as json]
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
      :body
      ;;bs/to-string
      ;;(json/parse-string true)
      ))
;;"Linux \"main fleet\" available container ratio is low"
;;"231670"
(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  #_(spit "hats.edn" (into [] (api-get "monitor" {}))))
