.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. SPDX-License-Identifier: CC-BY-4.0
.. Copyright (C) 2019 AT&T Intellectual Property

RIC Dashboard Overview
======================

The O-RAN SC RIC Dashboard provides administrative and operator
functions for a radio access network (RAN) controller.  The web app is
built as a single-page app using an Angular (version 8) front end and
a Java (version 11) Spring-Boot (version 2.1) back end.

Project Resources
-----------------

The project uses the following Linux Foundation resources.

* The source code is maintained in this Gerrit:
    `<https://gerrit.o-ran-sc.org/r/portal/ric-dashboard;a=summary>`_
* The build (CI) jobs are in this Jenkins:
    `<https://jenkins.o-ran-sc.org/view/portal-ric-dashboard>`_
* Issues are tracked in this Jira:
    `<https://jira.o-ran-sc.org/issues/?jql=project%3DOAM%20AND%20component%3D%22portal%2Fric-dashboard%22>`_
* Project information is available in this Wiki:
    `<https://wiki.o-ran-sc.org/display/OAM/RIC+Dashboard+Application>`_


Managed Resources
-----------------

The RIC Dashboard is used to manage the following RIC Platform and RIC
xApplication components.

Application Manager
~~~~~~~~~~~~~~~~~~~

The Dashboard interfaces with the Application Manager.  This platform
component is accessed via HTTP/REST requests using a client that is
generated from an API specification published by the Application
Manager team.

The Application Manager supports deploying, undeploying and
configuring applications in the RIC. The Dashboard UI provides screens
for these functions.


E2 Manager
~~~~~~~~~~

The Dashboard interfaces with the E2 Manager.  This platform
component is accessed via HTTP/REST requests using a client that is
generated from an API specification published by the E2 Manager team.

The E2 Manager platform component supports connecting and
disconnecting RAN elements.  The Dashboard UI provides controls for
operators to create "ENDC" and "X2" connections, and to disconnect RAN
elements.
