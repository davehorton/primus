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
	
for f in $SUSPEND_HOME/jars/*.jar
do
CP=${CP}:$f
done
	
java -cp $POS_HOME:$CP com.beachdog.App --dir $POS_HOME/reports $@
