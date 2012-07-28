#!/usr/bin/bash
#usage: runSuspend.sh -M6User user -M6Password pass -M6Address address -phoneNumber phone -[suspend|unsuspend]
date_regex='^[-|0][0-9]*$|^[0-9]{4}-[0-9]{2}-[0-9]{2}$'

usage () {
        cat <<EOF 
        Usage: `basename $0` -M6User user -M6Password pass -M6Address address -phoneNumber phone -[suspend|unsuspend]
EOF
exit -1
}

#set SUSPEND_HOME to the directory the script is running from
SUSPEND_HOME="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"


for f in $SUSPEND_HOME/jars/*.jar
do
CP=${CP}:$f
done
java -cp $SUSPEND_HOME:$CP com.primus.M6.App $@
