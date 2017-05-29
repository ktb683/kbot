(defproject kbot "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha15"]
                 [org.clojure/core.async "0.3.442"]
                 [cprop "0.1.10"]
                 [cheshire "5.7.1"]
                 [endophile "0.2.1"]
                 [hiccup "1.0.5"]
                 [me.raynes/cegdown "0.1.1"]
                 [me.raynes/conch "0.8.0"]
                 [buddy "1.3.0"]
                 ;;[org.clojure/core.async "0.3.442"]
                 [aleph "0.4.4-alpha2"]]
  :plugins [[lein-ancient "0.6.10"]]
  :main ^:skip-aot kbot.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
