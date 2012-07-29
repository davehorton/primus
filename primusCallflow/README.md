# prepaid local callflow

## Build

```bash
$ mvn install
```

## Install

### Drop to Java

The jar file built by this project, `prepaid-local-callflow-1.0.jar` provides the drop-to-Java API used by the TUI.  The project also contains the TUI callflow and the (test) prompts that it uses.  To make the jar file available to be used by the TUI, it must be included on the Java classpath along with primus-core-1.0.jar, which is built by the primusCore project, and the 3rd party libraries they are each dependent on.  Typically, the CLASSPATH variable is set by the `.bashrc` script for the `pcs` user, which is also sourced for the `root` user, and contains code like this to include all of the jar files in the `$PS_APPS_ROOT/jarfiles` directory:

```bash
export CLASSPATH=${CLASSPATH}:${PS_APPS_ROOT}/java

for f in $PS_APPS_ROOT/java/*.jar
do
export CLASSPATH=${CLASSPATH}:$f
done

for f in $PS_APPS_ROOT/jarfiles/*.jar
do
export CLASSPATH=${CLASSPATH}:$f
done
```
So, simply copy the two jar files mentioned above into the `$PS_APPS_ROOT/jarfiles` directory, along with the dependencies listed in the `pom.xml` file for this project.

### TUI

The callflow is provided in the `pl_ivr.xml` file that is provided in this project.  This file should be copied into the `$PS_ROOT/apps` directory and started automatically via the following lines in `$PS_ROOT\config\ps_master_config.xml` (note: change the number of sessions to an appropriate value for your licensing model):  

```xml
  <ps-appexec start-appserver-id="100" stop-appserver-id="110">
    <appserver id="100">
      <application sessions="50" iterations="0">pl_ivr.xml</application>
    </appserver>
  </ps-appexec>
```

### Prompts

Prompts should be installed as per usual under `$PS_ROOT\media\local\{language}\{codec}`; i.e. the prompts for each combination of language and codec will be contained in a separate directory.


 _




