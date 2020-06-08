.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. SPDX-License-Identifier: CC-BY-4.0
.. Copyright (C) 2019 AT&T Intellectual Property

RIC Dashboard Configuration and Deployment
==========================================

This documents the configuration and deployment of the O-RAN SC RIC
Dashboard web application, which is often deployed together with the
ONAP Portal.

Configuration
-------------

The application requires the following configuration files::

    application.yaml
    key.properties
    portal.properties

In Kubernetes deployment, all file contents are provided by a
configuration map. Construction of Helm charts, config maps and other
Kubernetes deployment resources is beyond the scope of this document.

Application Properties
^^^^^^^^^^^^^^^^^^^^^^

The file ``application.yaml`` must be provided when the application is
launched, either in the current working directory or in a ``config``
subdirectory (latter is preferred). For example, a Helm chart that
deploys the application should mount this file appropriately.

Many properties have default values cached within the application, in
file ``src/main/resources/application.yaml``.  Properties with default
values do NOT need to be repeated in a deployment-specific configuration.
Properties without default values MUST be specified in a
deployment-specific configuration.

The properties are listed below in alphabetical order with their fully
qualified dot-separated names, for example "server.port".  Please note
that in the YAML file, every component of the name is on a separate
line, for example::

    server:
        port: 8080
	
The application uses the following properties.

``appmgr.url.suffix``

Application Manager URL suffix. Default is ``/appmgr/ric/v1``.

``caasingress.aux.url.suffix``

CAAS-Ingress application URL suffix for the RIC Auxiliary cluster. Default is ``api``.

``caasingress.insecure``

Flag whether to disable SSL/TLS certificate and hostname verification.
If true, the dashboard can communicate with a CAAS-Ingress endpoint that
uses self-signed certificates.

``caasingress.plt.url.suffix``

CAAS-Ingress application URL suffix for the RIC-PLT cluster. Default is ``api``.

``e2mgr.url.suffix``

E2 Manager URL suffix. Default is ``/e2mgr/v1``.

``portalapi.appname``

Application name expected at ONAP portal. Default is ``RIC Dashboard``

``portalapi.decryptor``

Java class that decrypts ciphertext from Portal. Default is
``org.oransc.ric.portal.dashboard.portalapi.PortalSdkDecryptorAes``.

``portalapi.password``

REST password expected at ONAP portal. No default value.

``portalapi.security``

Boolean flag whether the Dashboard limits access to users (browsers)
that present security tokens set by the ONAP Portal.  If false, no
access control is performed, which is only appropriate for isolated
lab testing.

``portalapi.usercookie``

Name of request cookie with user ID. Default is ``UserId``.

``portalapi.username``

REST user name expected at ONAP portal. No default value.

``ricinstances.regions``

List of RIC region entries.  Each region has a name and a list of RIC
instances.  A region has entries as shown below, where the "[0]"
notation refers to the first instance in a list.  A partial example
appears next::

  ricinstances:
    regions:
        -
          name: Region AAA
          instances:
              -
                key: i1
                name: Primary RIC Instance
                appUrlPrefix: App prefix 1
                caasUrlPrefix: Caas prefix 1
                pltUrlPrefix: Plt prefix 1


``ricinstances.regions[0].name``

User-friendly name of the region.

``ricinstances.regions[0].instances[0].key``

Unique key for the instance, across all instances.

``ricinstances.regions[0].instances[0].name``

User-friendly name for the instance.

``ricinstances.regions[0].instances[0].appUrlPrefix``

xApplication URL prefix. In a Kubernetes deployment, this should be
the URL where an ingress service listens.  Usually a service
name like ``http://ricplt-entry/xapp``

``ricinstances.regions[0].instances[0].caasUrlPrefix``

CAAS-Ingress application URL prefix for the RIC Auxiliary cluster. 

``ricinstances.regions[0].instances[0].pltUrlPrefix``

RIC Platform URL prefix. In a Kubernetes deployment, this should be
the URL where an ingress service listens.  Usually a service name like
``http://ricplt-entry/xapp``

``server.port``

Port where the Tomcat server listens for requests. Default is
``8080``.

``statsfile``

Path of file that stores application statistic details. Default is
``dashboard-stats.json``.

``userfile``

Path of file that stores user details. Default is
``dashboard-users.json``.

``xappobrd.url.suffix``

Xapp Onboarder URL suffix. Default is ``/xapporbd/api/v1``.

Key Properties
^^^^^^^^^^^^^^

The file ``key.properties`` must be provided on the Java classpath for
the Spring-Boot application, as required by the EPSDK-FW library. The
Helm chart for the application should mount this file appropriately.
A sample file is in directory ``src/test/resources``.

The file must contain the following entries, listed here in
alphabetical order.

``cipher.enc.key``

Encryption key used by the EPSDK-FW library.  No default value.


Portal Properties
^^^^^^^^^^^^^^^^^

The file ``portal.properties`` must be provided on the Java classpath
for the application, as required by the EPSDK-FW library.  The Helm
chart for the application should mount this file appropriately.  A
sample file is in directory ``src/test/resources``.

The file must contain the following entries, listed here in
alphabetical order.

``ecomp_redirect_url``

Portal URL that is reachable by a user's browser.  This is a value
like
``https://portal.api.simpledemo.onap.org:30225/ONAPPORTAL/login.htm``

``ecomp_rest_url``

Portal REST URL that is reachable by the Dashboard back-end.
This is a value like ``http://portal-app.onap:8989/ONAPPORTAL/auxapi``

``portal.api.impl.class``

Java class name.  No default value.  Value must be
``org.oransc.ric.portal.dashboard.portalapi.PortalRestCentralServiceImpl``

``role_access_centralized``

Selector for role access.  No default value.  Value must be ``remote``.

``ueb_app_key``

Unique key assigned by ONAP Portal to the RIC Dashboard application.
No default value.


Deployment
----------

A production server requires the configuration files listed above.
All files should be placed in a ``config`` directory.  That name is
important; Spring automatically searches that directory for the
``application.yaml`` file. Further, that directory can easily be
placed on the Java classpath so the additional files can be found at
runtime.


On-Board Dashboard to ONAP Portal
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

When on-boarding the Dashboard to the ONAP Portal the administrator
must supply the following information about the deployed instance:

- Dashboard URL that is reachable by a user's browser. The domain of
  this host name must match the Portal URL that is similarly reachable
  by a user's browser for cookie-based authentication to function as
  expected.  This should be a value like
  ``http://dashboard.simpledemo.onap.org:8080``
- Dashboard REST URL that is reachable by the Portal back-end server.
  This can be a host name or an IP address, because it does not use
  cookie-based authentication.  This must be a URL with suffix "/api/v3"
  for example ``http://192.168.1.1:8080/api/v3``.

The Dashboard server only listens on a single port, so the examples
above both use the same port number.  Different port numbers might be
required if an ingress controller or other proxy server is used.

After the on-boarding process is complete, the administrator must
enter values from the Portal for the following properties explained
above:

- ``portalapi.password``
- ``portalapi.username``
- ``ueb_app_key``

Launch Server
^^^^^^^^^^^^^

After creating, populating and mounting Kubernetes config maps
appropriately, launch the server with this command-line invocation to
include the ``config`` directory on the Java classpath::

    java -cp config:target/ric-dash-be-2.0.1-SNAPSHOT.jar \
        -Dloader.main=org.oransc.ric.portal.dashboard.DashboardApplication \
        org.springframework.boot.loader.PropertiesLauncher
