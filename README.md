# primus

This project contains the source code created by Beachdog Networks under contract to Primus Telecom.  

## Install
From within each of the project directories below, run the pom.xml as follows:
```bash
$mvn install
```
The pom.xml files in each project contain dependencies for all the third-party jar files required.  

With the exception of a Broadsoft jar required for M6 integration, these jars are all available in public maven repositories you may find on the internet.  However, a copy of all of these dependencies -- including the Broadsoft jar -- are available on the beachdognet maven repository, and by copying the provided `example-maven-settings.xml` into your ~/.m2 directory as `settings.xml` you will automatically access these when running a maven build.  

In other words, by using this as your settings.xml you should be able 
to build all projects on any machine of your liking (that has internet access) with no dependencies so long as it has a JDK 1.6 or above and maven has been installed on it.

##Projects

### M6-Suspend

This project provides a command-line utility that can be used to suspend or unsuspend accounts on the M6 without any other side-affects.  It is meant to mainly be used as a test assist tool.  See the `src/bin/runsuspend.sh` file for an example of how it is intended to be used.

### java_framework

This project provides functionality for logging and database connection management that is used in several projects.

### posReporting

This project provides a point-of-sale reporting facility with increased functionality required for the prepaid local application.

### prepaidLocalWS

This project provides a web service interface, defined by a WSDL (Web Services Description Language) document, that exposes recharge functionality for prepaid local subscribers.

### primusCallflow

This project contains the TUI (telephony user interface) components of the prepaid local application.

### primusCore

This project contains core database functionality that is used by several of the other primus components.

### suspendZeroBalanceAccounts

This project provides a nightly batch job that is used to suspend prepaid local accounts on the M6 gateway when the subscriber account balance is zero and has met several other conditions.



