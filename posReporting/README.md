# point of sale reporting

## Install

You can run this report from any directory.  Create the following directory structure:
<code>  
POS_HOME  
|  
|--beans.xml  
|--log4j.properties  
|--runReport.sh  
|--jars/  
|--logs/  
|--reports  
</code>

In the `jars` directory the following jars should be placed:  
<code>
pos-reporting-1.0.jar  
prepaid-local-core-1.0.jar  
java_framework-1.0.jar    
log4j-1.2.15.jar  
commons-pool-1.3.jar  
commons-dbcp-1.2.2.jar  
commons-beanutils-1.8.3.jar  
asm-1.5.3.jar  
cglib-2.1_3.jar  
asm-attrs-1.5.3.jar  
dom4j-1.6.1.jar  
commons-logging-1.1.1.jar  
jta-1.1.jar  
Oracle-11.1.0.6.jar  
slf4j-simple-1.5.8.jar  
slf4j-api-1.5.8.jar  
spring-2.5.6.jar  
commons-collections-3.2.1.jar  
hibernate-3.2.6.ga.jar  
</code>  

Edit the beans.xml file to point to the correct database by changing the second line below, if necessary:
```xml  
<property name="connectionURL">  
       <value>jdbc:oracle:thin:@10.201.174.136:1521:psprd1</value>   
</property>  
````

Edit the log4j.properties file to point to a log file and directory of your choosing, by editing this line:

`
log4j.appender.A2.File=/export/home/pcs/newPosReporting/logs/posReporting.log
`

Make sure the file named 'runReport.sh' is executable:
```bash
chmod +x runReport.sh
```
`
Now you are ready to run the report.  To run a report for yesterday, execute the following command:
```bash
./runReport.sh --start -1 --end 0
```

To view the possible date options, simply run the script with no parameters:

```bash
./runReport
```

If desired, create a cron entry similar to the one below to run the report each morning:

```bash
30 03 * * * /usr/bin/bash -c "/export/home/pcs/newPosReporting/runReport.sh >/export/home/pcs/newPosReporting/logs/newPosReporting.out 2>&1"
```