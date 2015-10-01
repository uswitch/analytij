# analytij

Clojure library for accessing the Google Analytics API.

## Usage

```clojure
(use 'analytij.auth)
(use 'analytij.management)

(def service-account-id "XXX@developer.gserviceaccount.com")

(def s (service service-account-id "./path-to-creds.p12"))

```

## License

Copyright © 2015 uSwitch Limited

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
