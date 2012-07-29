# M6-suspend

## Build

```bash
$ mvn install
```

## Install

1. Create a top-level directory and copy the `src\bin\runM6suspend.sh' file into it.  
1. Make the file executable by `chmod +x runM6Suspend.sh`.
1. Create a jars directory underneath the top level directory
1. Copy the dependent jars listed in the pom.xml into the `jars` directory.
1. Run the script as follows:

```bash
./runM6Suspend.sh -M6User user -M6Password pass -M6Address address -phoneNumber phone -suspend|unsuspend
```
