(ns analytij.management
  (:import [com.google.api.client.googleapis.auth.oauth2 GoogleCredential$Builder]
           [com.google.api.client.googleapis.javanet GoogleNetHttpTransport]
           [com.google.api.client.json.jackson2 JacksonFactory]
           [com.google.api.services.analytics AnalyticsScopes Analytics$Builder])
  (:require [clojure.java.io :as io]))

(defn- service-credentials
  [account-id private-key-file]
  (let [credential (doto (GoogleCredential$Builder. )
                     (.setTransport (GoogleNetHttpTransport/newTrustedTransport))
                     (.setJsonFactory (JacksonFactory. ))
                     (.setServiceAccountId account-id)
                     (.setServiceAccountScopes [AnalyticsScopes/ANALYTICS])
                     (.setServiceAccountPrivateKeyFromP12File (io/file private-key-file)))]
    (.build credential)))


(defn- service
  [account-id private-key-file]
  (let [creds (service-credentials account-id private-key-file)
        analytics (doto
                      (Analytics$Builder. (GoogleNetHttpTransport/newTrustedTransport) (JacksonFactory.) creds)
                    (.setApplicationName "Analytij")
                    (.setHttpRequestInitializer creds))]
    (.build analytics)))


(defn- to-stream
  [csv]
  (let [file (io/file csv)
        file-input-stream (io/input-stream file)
        media-content (InputStreamContent. "application/octet-stream" file-input-stream)]
    (doto media-content (.setLength (.length file)))
    media-content))

(defn upload-data
  [account-id property-id custom-data-source-id private-key-file cost-data-file]
  (let [analytics   (service account-id private-key-file)
        cost-data   (to-stream cost-data-file)
        management  (.management analytics)
        uploads     (.uploads management)
        request     (doto uploads (.uploadData account-id property-id custom-data-source-id cost-data))
        response    (.execute request)]
    (println response)))
