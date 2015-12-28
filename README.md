# analytij

Clojure library for accessing the Google Analytics API, for example
the
[Core Reporting API](https://developers.google.com/analytics/devguides/reporting/core/v3/). Google
Analytics does not expose - or rather document - a public HTTP API, but 
implements their own clients in
[various programming languages](https://developers.google.com/analytics/devguides/reporting/core/v3/gdataLibraries).

Most Google Analytics APIs like the Core Reporting API come for many
languages, but mostly the Java, Python and PHP versions are
mature. All other languages are either in Alpha or Beta state.

analytij is a wrapper over a stable and current version of the Java
library and adds a thin layer of Clojure abstraction over it. This
makes the APIs easy to read and very accessible to testing using
REPL-driven development.

If you are unfamiliar with the Google Analytics API and its
parameters, use the
[Google Query explorer](https://ga-dev-tools.appspot.com/query-explorer/)
which is a web interface to different kinds of Google Analytics APIs
where you can easily test out different kinds of queries and
parameters.

## Usage

```clojure
(use 'analytij.auth)
(use 'analytij.management)

(def service-account-id "XXX@developer.gserviceaccount.com")

(def s (service service-account-id "./path-to-creds.p12"))

```

## Examples

Please see code examples in the [doc/examples](doc/examples) folder.

## License

Copyright © 2015 uSwitch Limited

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
