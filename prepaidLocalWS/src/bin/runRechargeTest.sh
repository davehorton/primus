#!/usr/bin/bash
#usage: runRechargeTest.sh

#set TEST_HOME to the directory the script is running from
TEST_HOME="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

for f in $TEST_HOME/jars/*.jar
do
CP=${CP}:$TEST_HOME:$f
done
java -cp $CP com.beachdog.primusCore.App $@