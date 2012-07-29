# prepaid local web service

## Build

```bash
$ mvn install
```

## Install

Copy the prepaidLocal-3.0.war generated from the build step into the `$TOMCAT_HOME\webapps` directory on the target server.  After starting Tomcat, edit the `$TOMCAT_HOME\webapps\prepaidLocal-3.0\WEB-INF\classes\beans.xml`
if necessary to change the settings for the web service endpoint and the database ip address.  If it is necessary to change this information, then be sure to restart the web application after doing so.

Also note that logging settings are configured by the `$TOMCAT_HOME\webapps\prepaidLocal-3.0\WEB-INF\classes\log4j.properties`.  If it is necessary to modify these settings, be sure to restart the web application after doing so.


