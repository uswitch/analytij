(ns analytij.management
  (:import [com.google.api.client.http InputStreamContent])
  (:require [clojure.java.io :as io]))

(defn- to-stream
  [csv]
  (let [file (io/file csv)
        file-input-stream (io/input-stream file)
        media-content (InputStreamContent. "application/octet-stream" file-input-stream)]
    (doto media-content (.setLength (.length file)))))

(defn upload-response->map [response]
  {:account-id     (.getAccountId response)
   :data-source-id (.getCustomDataSourceId response)
   :id             (.getId response)
   :status         (.getStatus response)})

(defn upload-data
  [analytics-service account-id property-id custom-data-source-id cost-data-file]
  (let [cost-data   (to-stream cost-data-file)
        management  (.management analytics-service)
        uploads     (.uploads management)
        request     (.uploadData uploads account-id property-id custom-data-source-id cost-data)
        response    (.execute request)]
    (upload-response->map response)))

(defn upload-status->map [status]
  {:status         (.getStatus status)
   :account-id     (.getAccountId status)
   :data-source-id (.getCustomDataSourceId status)
   :id             (.getId status)})

(defn upload-status [analytics-service account-id property-id data-source-id upload-id]
  (let [uploads (.. analytics-service management uploads)]
    (upload-status->map (.execute (.get uploads account-id property-id data-source-id upload-id)))))



(defn accounts
  [service]
  (let [list-request (.. service management accounts list)]
    (->> (get (.execute list-request) "items")
         (map (fn [result]
                {:id   (.getId result)
                 :name (.getName result)})))))
