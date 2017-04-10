(ns kbot.core
  (:require
   [manifold.deferred :as d]
   [aleph.http :as http]
   [byte-streams :as bs])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]

  ;; Basic get request.
  (-> @(http/get "https://google.com/"
                 {;:oauth-token ""
                  :throw-exceptions false
                  :as :json})
      :body
      bs/to-string
      prn))
