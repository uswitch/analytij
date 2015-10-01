(ns analytij.auth
  (:require [clojure.java.io :as io])
  (:import [com.google.api.client.googleapis.auth.oauth2 GoogleCredential$Builder]
           [com.google.api.client.googleapis.javanet GoogleNetHttpTransport]
           [com.google.api.client.json.jackson2 JacksonFactory]
           [com.google.api.services.analytics Analytics$Builder AnalyticsScopes]))

(defn- service-credentials
  [account-id private-key-file]
  (let [credential (doto (GoogleCredential$Builder. )
                     (.setTransport (GoogleNetHttpTransport/newTrustedTransport))
                     (.setJsonFactory (JacksonFactory. ))
                     (.setServiceAccountId account-id)
                     (.setServiceAccountScopes [AnalyticsScopes/ANALYTICS])
                     (.setServiceAccountPrivateKeyFromP12File (io/file private-key-file)))]
    (.build credential)))

(defn service
  [account-id private-key-file]
  (let [creds (service-credentials account-id private-key-file)
        analytics (doto
                      (Analytics$Builder. (GoogleNetHttpTransport/newTrustedTransport) (JacksonFactory.) creds)
                    (.setApplicationName "Analytij")
                    (.setHttpRequestInitializer creds))]
    (.build analytics)))
