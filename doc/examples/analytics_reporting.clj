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

(def service-account-id "vr-ga-123@organic-spirit-123.iam.gserviceaccount.com")

(def ga-service (service service-account-id "client_secrets.p12"))

;; The unique ID used to retrieve the Analytics data. This ID is the
;; concatenation of the namespace ga: with the Analytics view
;; (profile) ID.
(def view-id "ga:12312312")

(def start-date
  (java.util.Date. "11/22/2015"))

(def end-date
  (java.util.Date. "11/23/2015"))


;;; Example: Get the session count for a specific period of time
(defn get-sessions-from-ga
  ""
  [start-date end-date]
  (analytij.data/execute ga-service {
                                     :start-date start-date
                                     :end-date end-date
                                     :metrics ["ga:sessions"]
                                     :view-id view-id }))

(defn get-session-count
  ""
  [sessions]
  (map :value (first (:records sessions))))

;; example output: (1155)
(def sessions (get-session-count (get-sessions-from-ga start-date end-date)))


;;; Example: Get a count of specific events.
;;; This example is taken from a website that has "talks" that can be
;;; "played" on the web. Therefore, there are event labels in the form
;;; of "talk:\d+" which are filtered by the event action label "play".
(defn get-talk-play-counts-from-ga
  ""
  [start-date end-date]
  ;; Sample Output for this request: ("talk:1125" 1 "talk:1126" 4) 
  (analytij.data/execute ga-service { :start-date start-date
                                     :end-date end-date
                                     :metrics ["ga:totalEvents"]
                                     :dimensions ["ga:eventLabel"]
                                     :filters "ga:eventAction==play"
                                     :view-id view-id }))

(defn map-play-counts-from-ga [play-count-list]
  ;; This expression pretty much looks like this example:
  ;; (map :value (flatten (quote (({:value "foo"}) ({:value "bar"}))) ))
  (map :value (flatten (:records play-count-list))))

(defn list-to-map
  "Takes a list of names and values and converts them into a
  list of maps.
   Example usage: (list-to-map '(\"talk:1125\" 1 \"talk:1126\" 4))"
  [input-list]
  (map #(reduce (fn[k v]
                  (assoc {} :name k :value v))
                %)
       (partition 2 input-list)))

(defn get-talk-play-counts [start-date end-date]
  (list-to-map (map-play-counts-from-ga (get-talk-play-counts-from-ga start-date end-date))))

;; example output: ({:name "talk:1125", :value 1} {:name "talk:1126", :value 4})
(def play-counts (get-talk-play-counts start-date end-date))
