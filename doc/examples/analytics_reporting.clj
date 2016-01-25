;;;; This file shows query examples for the Google Analytics Core
;;;; Reporting API.

;;;; For these examples to run, please follow Step 1 of the Google
;;;; Analytics Core Reporting Java API documentation:
;;;; https://developers.google.com/analytics/devguides/reporting/core/v3/quickstart/service-java
(ns analytics-examples.core)

(use 'analytij.auth)
(use 'analytij.data)
(use 'analytij.management)

;;; General setup

(def service-account-id "some@xxx.gserviceaccount.com")
(def ga-service         (service service-account-id "secret.p12"))

;; The unique ID used to retrieve the Analytics data. This ID is the
;; concatenation of the namespace ga: with the Analytics view
;; (profile) ID.
(def view-id "ga:12312312")

(def start-date (java.util.Date. "11/22/2015"))
(def end-date   (java.util.Date. "11/23/2015"))


;; All data is retrieved using a query, specifying the dimensions,
;; metrics and other parameters. A good reference is this:
;; https://developers.google.com/analytics/devguides/reporting/core/dimsmets
(def results (analytij.data/execute ga-service {:start-date start-date
                                                :end-date   end-date
                                                :view-id    view-id
                                                :metrics    ["ga:sessions"]
                                                :dimensions ["ga:medium"]
                                                :filters    "ga:pagePath=~first"}))

(comment
  ;; result looks like this:
  {:total-results 5
   :columns       [{"columnType" "DIMENSION", "dataType" "STRING", "name" "ga:medium"}
                   {"columnType" "METRIC", "dataType" "INTEGER", "name" "ga:sessions"}]
   :sampled? false
   :records       (({:name "ga:medium", :column-type "DIMENSION", :value "(none)"}
                    {:name "ga:sessions", :column-type "METRIC", :value 10})
                   ({:name "ga:medium", :column-type "DIMENSION", :value "(not set)"}
                    {:name "ga:sessions", :column-type "METRIC", :value 1})
                   ({:name "ga:medium", :column-type "DIMENSION", :value "banner"}
                    {:name "ga:sessions", :column-type "METRIC", :value 100})
                   ({:name "ga:medium", :column-type "DIMENSION", :value "cpc"}
                    {:name "ga:sessions", :column-type "METRIC", :value 15})
                   ({:name "ga:medium", :column-type "DIMENSION", :value "display"}
                    {:name "ga:sessions", :column-type "METRIC", :value 20}))})
