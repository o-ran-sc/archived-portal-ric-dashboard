.. ===============LICENSE_START=======================================================
.. O-RAN SC CC-BY-4.0
.. %%
.. Copyright (C) 2019 AT&T Intellectual Property and Nokia
.. %%
.. Licensed under the Apache License, Version 2.0 (the "License");
.. you may not use this file except in compliance with the License.
.. You may obtain a copy of the License at
..
..      http://www.apache.org/licenses/LICENSE-2.0
..
.. Unless required by applicable law or agreed to in writing, software
.. distributed under the License is distributed on an "AS IS" BASIS,
.. WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
.. See the License for the specific language governing permissions and
.. limitations under the License.
.. ===============LICENSE_END=========================================================

===========================
RIC Dashboard Configuration
===========================

This documents the configuration parameters for the O-RAN SC RIC
Dashboard web application.

Application Properties
----------------------

This Spring-Boot project reads parameters from file
``application.properties`` in the current working directory when
launched, or from the same file in a ``config`` subdirectory. The
application properties are listed in alphabetical order. Most have
default values, but not all.

``a1med.url.prefix``

A1 Mediator URL prefix.  Usually a service name like
"http://ricplt-entry/a1mediator"

``a1med.url.suffix``

A1 Mediator URL suffix; default is empty.

``anrxapp.url.prefix``

ANR Application URL prefix.  Usually a service name like
"http://ricxapp-entry/anr"

``anrxapp.url.suffix``

ANR Application URL suffix; default is empty.

``appmgr.url.prefix``

Application Manager URL prefix. Usually a service name like
"http://ricplt-entry/appmgr"

``appmgr.url.suffix``

Application Manager URL suffix. Default is "/ric/v1".

``e2mgr.url.prefix``

E2 Manager URL prefix. Usually a service name like
"http://ricplt-entry/e2mgr". 

``e2mgr.url.suffix``

E2 Manager URL suffix. Default is "/v1".

``mock.config.delay``

Sleep period for mock methods in milliseconds.  This mimics slow
endpoints. Default is 0.


``portalapi.appname``

Application name expected at ONAP portal; default "RIC Dashboard"

``portalapi.decryptor``

Java class that decrypts ciphertext from Portal; default
"org.oransc.ric.portal.dashboard.portalapi.PortalSdkDecryptorAes"

``portalapi.password``

Application password expected at ONAP portal; no default.

``portalapi.usercookie``

Name of request cookie with user ID; default "UserId"

``portalapi.username``

Application user name expected at ONAP portal; no default.

``server.port``

Tomcat server port, default 8080

``userfile``

Path to file that stores user details; default "users.json"

``stats.acappmetrics.url``

Url to the kibana source which visualizes AC App metrics. This needs to be replaced with actual value during deployment time.


Key Properties
--------------

The file ``key.properties`` must be found on the Java classpath.  It
must contain the following entries.

``cipher.enc.key``

Encryption key used by the EPSDK-FW library.
      

Portal Properties
-----------------

The file ``portal.properties`` must be found on the Java classpath.
It must contain the following entries.

``ecomp_redirect_url``

URL of ONAP Portal.  Usually a value like
"https://portal.api.simpledemo.onap.org:30225/ONAPPORTAL/login.htm"

``ecomp_rest_url``

URL of ONAP Portal REST endpoint.  Usually a value like
"http://portal-app.onap:8989/ONAPPORTAL/auxapi"

``portal.api.impl.class``

Java class name.  Default is "org.oransc.ric.portal.dashboard.portalapi.PortalRestCentralServiceImpl"

``role_access_centralized``

Default value is "remote".

``ueb_app_key``

Unique key assigned by ONAP Portal to the RIC Dashboard application.
No default.
