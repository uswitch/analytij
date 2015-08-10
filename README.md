# analytij

Clojure library to ease interacting with the Google Analytics API. It is built upon [Google's Analytics API Java Client Library](https://developers.google.com/api-client-library/java/apis/analytics/v3).

Current [version](http://mvnrepository.com/artifact/com.google.apis/google-api-services-analytics/v3-rev116-1.20.0)

Current functionality 
- Upload cost data csv to an Google analytics account with an existing property. Property must have a view with a custom-data-source associated with it.
- See upload status of a csv given you know the account-id, property-id, data-source-id and upload-id.

## Usage

You need a [service account](https://developers.google.com/console/help/?csw=1#service_accounts) to use analytij.
 
Instructions copied/inspired from [legato](https://github.com/tpitale/legato/wiki/OAuth2-and-Google#service-accounts) *Awaiting approval*

#Terminology
- *G.A*                    - [Google analytics](https://www.google.co.uk/analytics/)
- *analytics-service*      - This is created by the service fn. 
- *account-id*             - This is your google analytics account id (you can see this in Account Settings in G.A)
- *property-id*            - This is the property id listed inside G.A admin tab when you click on the property you want. 
- *custom-data-source-id*  - This is created when you click on Data import in the property section
- *cost-data-file*         - This is your csv file containing your cost data.

# Service Accounts

**Note**: Service accounts only work for Google Apps accounts. With a regular @gmail.com google account, you'll need an "Installed Application". 

## Registering for API Access

* Go to the [Google API Console](https://code.google.com/apis/console/)
* Create a new Project and name it accordingly
* Turn on the **Analytics API** access by clicking APIs and Auth.
* Click APIs
* Search for **Analytics API** click through and enable
* Click **Credentials**
* Click **Create an OAuth**
    * Select Service Account
    * Click create client id
* Private key should be automatically downloaded, keep it somewhere safe
* Note the @developer.gserviceaccount.com email address that is displayed under the **Service account** section of the page.
* Go to Google Analytics
* Click Admin
* Click the Users tab
* Add a new user with that email address.
* Read and Analyse Account Permissions should be default

More info on [service accounts](https://developers.google.com/console/help/?csw=1#service_accounts).
 

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
