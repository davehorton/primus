#!/usr/bin/bash
#usage: runReport.sh --start <start> --end <end>
#where:
#       <start> - a start date in yyyy-mm-dd format, or else a negative integer representing a nuber of days in the past
#       <end> - an end date in yyyy-mm-dd format, or else a non-positive integer representing a number of days in the past
#
#       note: the date range selected for the report will be from the start date to the end date, inclusive of the start date but NOT inclusive of the end date
#
#       examples:
#
#               runReport.sh --start -21 --end 0                        #runs report starting 21 days ago through yesterday
#               runReport.sh --start -1 --end 0                         #runs report for yesterday
#               runReport.sh --start 2012-01-01 --end 2012-02-01        #runs report for month of January 2012

date_regex='^[-|0][0-9]*$|^[0-9]{4}-[0-9]{2}-[0-9]{2}$'

usage () {
        cat <<EOF 
        Usage: `basename $0` --start startValue --end endValue
        where:
              startValue - a start date in yyyy-mm-dd format, or else a negative integer representing a nuber of days in the past
              endValue   - an end date in yyyy-mm-dd format, or else a non-positive integer representing a number of days in the past
        
        note: the date range selected for the report will be from the start date to the end date, inclusive of the start date but NOT inclusive of the end date
        
              examples:

              `basename $0` --start -21 --end 0                        #runs report starting 21 days ago through yesterday
              `basename $0` --start -1 --end 0                         #runs report for yesterday
              `basename $0` --start 2012-01-01 --end 2012-02-01        #runs report for month of January 2012
EOF
exit -1
}

validateDate () {
	if [[ $1 =~ $date_regex ]]; then
		return
	fi
    usage
}

#set POS_HOME to the directory the script is running from
#note: subdirectories should be: reports, jars, and logs;  beans.xml and log4j.properties should be found in $POS_HOME
POS_HOME="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

#validate arguments
declare -i hasStart=0
declare -i hasEnd=0
declare -i processingStart=0
declare -i processingEnd=0
for i; 	
	do  
		if [ "$processingStart" -eq 1 ]; then
			processingStart=0
			hasStart=1
			validateDate $i
        elif [ "$processingEnd" -eq 1 ]; then
			    processingEnd=0
			    hasEnd=1
			    validateDate $i
		elif [ $i = '--start' ]; then
			    processingStart=1
		elif [ $i = '--end' ]; then
			processingEnd=1
		fi
	done

	if [[ "$hasStart" -ne 1  || "$hasEnd" -ne 1 ]] ; then
		usage 
	fi	
	
java -cp $POS_HOME:$POS_HOME/posReporting-1.0.jar:$POS_HOME/primusCore-1.0.jar:$POS_HOME/java_framework-1.0.jar:$POS_HOME/jars/log4j-1.2.15.jar:$POS_HOME/jars/commons-pool-1.3.jar:$POS_HOME/jars/commons-dbcp-1.2.2.jar:$POS_HOME/jars/commons-beanutils-1.8.3.jar:$POS_HOME/jars/Oracle-11.1.0.6.jar:$POS_HOME/jars/commons-collections-3.2.1.jar:$POS_HOME/jars/hibernate-3.2.6.ga.jar:$POS_HOME/jars/spring-2.5.6.jar:$POS_HOME/jars/commons-logging-1.1.1.jar:$POS_HOME/jars/dom4j-1.6.1.jar:$POS_HOME/jars/jta-1.1.jar:$POS_HOME/jars/slf4j-api-1.5.8.jar:$POS_HOME/jars/slf4j-simple-1.5.8.jar:$POS_HOME/jars/cglib-2.1_3.jar:$POS_HOME/jars/asm-1.5.3.jar:$POS_HOME/jars/asm-attrs-1.5.3.jar com.beachdog.App --dir $POS_HOME/reports $@
