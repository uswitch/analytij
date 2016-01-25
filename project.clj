(defproject analytij "0.3.3"
  :description "Clojure client to interact with Google Analytics API"
  :url "https://github.com/uswitch/analytij"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [prismatic/schema "1.0.1"]
                 [clj-time "0.11.0"]
                 [com.google.api-client/google-api-client "1.20.0"]
                 [com.google.apis/google-api-services-analytics "v3-rev116-1.20.0"]])
