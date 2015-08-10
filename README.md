# analytij

A Clojure wrapper around the https://developers.google.com/analytics/ SDK. 
Current [version](http://mvnrepository.com/artifact/com.google.apis/google-api-services-analytics/v3-rev116-1.20.0)

Current functionality 
- Upload cost data csv to an account/property/custom-data-source
- See upload status of a csv

## Usage

You need a [service account](https://developers.google.com/console/help/?csw=1#service_accounts) to use analytij.
 
Instructions copied/inspired from [legato](https://github.com/tpitale/legato/wiki/OAuth2-and-Google#service-accounts) *Awaiting approval*

# Service Accounts

**Note**: Service accounts only work for Google Apps accounts. With a regular @gmail.com google account, you'll need an "Installed Application". 

## Registering for API Access

* Go to the [Google API Console](https://code.google.com/apis/console/)
* Create a new Project and name it accordingly
* Turn on the **Analytics API** access
* Click **API Access** in the left column
* Click **Create an OAuth**
    * Enter a product name, and add an optional logo
    * Click next
    * Select Service Account
    * Click create client id
* Download the private key, and keep it somewhere safe
* Note the @developer.gserviceaccount.com email address that is displayed under the **Service account** section of the page.
* Go to Google Analytics
* Click Admin
* Click the Users tab
* Add a new user with that email address.

More info on [service accounts](https://developers.google.com/console/help/?csw=1#service_accounts).
 

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
