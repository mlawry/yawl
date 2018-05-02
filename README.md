# YAWL (Yet Another Workflow Language)

YAWL is a BPM/Workflow system, based on a concise and powerful modelling language, that handles complex data transformations, and full integration with organizational resources and external Web Services. 

### Major Features
YAWL offers these distinctive features:

* the most powerful process specification language for capturing control-flow dependencies and resourcing requirements.
* native data handling using XML Schema, XPath and XQuery.
* a formal foundation that makes its specifications unambiguous and allows automated verification.
* a service-oriented architecture that provides an environment that can easily be tuned to specific needs.
* YAWL has been developed independent from any commercial interests. It simply aims to be the most powerful language for process specification.
* For its expressiveness, YAWL offers relatively few constructs (compare this e.g. to BPMN!).
* YAWL offers unique support for exception handling, both those that were and those that were not anticipated at design time.
* YAWL offers unique support for dynamic workflow through the Worklets approach. Workflows can thus evolve over time to meet new and changing requirements.
* YAWL aims to be straightforward to deploy. It offers a number of automatic installers and an intuitive graphical design environment.
* YAWL's architecture is Service-oriented and hence one can replace existing components with one's own or extend the environment with newly developed components.
* The YAWL environments supports the automated generation of forms. This is particularly useful for rapid prototyping purposes.
* Tasks in YAWL can be mapped to human participants, Web Services, external applications or to Java classes.
* Through the C-YAWL approach a theory has been developed for the configuration of YAWL models. For more information on process configuration visit [www.processconfiguration.com]
* Simulation support is offered through a link with the [ProM](www.processmining.org) environment. Through this environment it is also possible to conduct post-execution analysis of YAWL processes (e.g. in order to identify bottlenecks).

### Other Features
* new: completely rewritten Process Editor
* new: Auto Update + Install/Uninstall of selected components
* delayed case starting
* support for passing files as data
* support for non-human resources
* support for interprocess communication
* calendar service and scheduling capabilities
* task documentation facility
* revised logging format and exporting to OpenXES
* integration with external applications
* custom forms
* sophisticated verification support
* Web service communication
* Highly configurable and extensible

## About This Fork

This fork aims to provide a Maven-based build script that will create the JAR files
in the **yawllib** folder of a YAWL installation. Note the resulting JAR files *should*
be equivalent to the official builds but the code may actually be different.

* Install YAWL 4.1 using the official installers mentioned in http://www.yawlfoundation.org/

* The official JAR files are found in the folder **installdir/engine/apache-tomcat-7.0.65/yawllib**

* Follow the steps below to build the equivalent JAR files using this fork. Requires a Linux
  system with git, bash shell and maven installed. Also requires access to maven central repository.

* Git clone this fork into a new folder named **yawl**

* `cd yawl/build/jar`

* `bash build-yawl-lib.sh`

* When the script ends, `rm *.jar` from the **yawllib** folder of your YAWL installation.

* Copy **yawl/build/jar/yawl-lib/target/yawl-lib-4.1.jar** to **yawllib**

* Copy **yawl/build/jar/yawl-lib/target/lib/*.jar** to **yawllib**

* Your new **yawllib** is now ready. Start tomcat and test it out.

NOTE: Only the yawl service (the engine) and the resourceService are known to work, and
there may still be certain class files missing. Please create an issue in GitHub if you find problems.

