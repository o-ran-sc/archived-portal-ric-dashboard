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

RIC Dashboard Release Notes
===========================

Version 1.2.1, ? Aug 2019
-------------------------
* Add EPSDK-FW user management and Portal security

Version 1.2.0, 21 Aug 2019
--------------------------
* Split URL properties into prefix/suffix parts
* Add jacoco plugin to back-end for code coverage
* Compile with Java version 11, use base openjdk:11-jre-slim
* Clean code of issues reported by Sonar
* Drop mock RAN names feature that supported R1 testing
* Extend mock endpoints to simulate delay seen in tests
* Move mock configuration classes into test area
* Update A1 mediator client to spec version 0.10.0
* Update App manager client to spec version 0.1.7
* Update E2 manager client to spec version 20190815
* Add controller for page refresh of Angular routes
* Extend E2 mock configuration for demo purposes
* Add pattern for matching AC/admin application name
* Add custom (plain but not white-label) error page
* Synch A1 method paths in front-end and back-end
* Add xapp dynamic configuration feature
* Disable x-frame-options response header
* Repair app manager undeploy-app method
* Display AC xAPP metrics data via Kibana source (metrics.url.ac) on dashboard
* Pass AC policy parameter without parsing as JSON

Version 1.0.5, 5 July 2019
--------------------------
* Upgrade to Angular version 8
* Upgrade to Spring-Boot 2.1.6.RELEASE
* Align AC xApp policy page title
* Update E2 manager client to spec version 20190703
* Add configuration-driven mock of E2 getNodebIdList
* Revise front-end components to use prefix 'rd'
* Improve error handling in BE and FE code
* Revise the notification service to display multiple notifications
* Add JUnit test cases for controller methods

Version 1.0.4, 27 June 2019
---------------------------
* Add AC xApp neighbor control screen
* Add ANR xApp neighbor cell relation table
* Drop the pendulum xApp control screen
* Add column sorting on xApp catalog, xApp control, ANR
* Add disconnect-all button to RAN connection screen
* Extend E2 service with disconnect-all method
* Update ANR xApp client to spec version 0.0.8
* Update E2 manager client to spec version 20190620
* Adjust CSS and HTML for main container positioning
* Revise config property keys to use URL (not basepath)
* Left menu overlap main content fix
* Extend back-end controllers to return error details
* Add feature resilient to malformed instance data
* Extend Xapp Controller with config endpoints
* Add build number to dashboard version string
* Move mock admin screen user data to backend
* Update App manager client to spec version 0.1.5
* Move RAN connection feature to control screen
* Rework admin table
* Update the notification service
* Move RAN connection feature to control screen
* Repair deploy-app feature and use icon instead of text button

Version 1.0.3, 28 May 2019
--------------------------
* Add AC xApp controller to backend
* Add AC xApp interface to frontend
* Add RAN type radio selector to connection setup
* Update ANR xApp client to spec version 0.0.7
* Update E2 manager client to spec version 20190515
* Update xApp manager client to spec version 0.1.4
* Add get-version methods to all controllers
* Add simple page footer with copyright and version
* Add AC and ANR xApp services
* Rename signal service to E2 Manager service
* Use XappMgrService to replace ControlService and CatalogService
* Apply mat-table to control and catalog
* RAN Connection screen upgrade to mat-table

Version 1.0.2, 13 May 2019
--------------------------
* Update A1 mediator client to version 0.4.0
* Add E2 response message with timestamp and status code
* Fetch xAPP instance status information from xAPP Manager and display in dashboard
* Allow the user to initiate an E2 (X2) connection between RIC and gNB/eNB
* User input validations on connections between RIC and eNB/gNB in the dashboard
* Add ANR xApp backend with mock implementation
* Add undeploy xApp function
* Add shared confirm dialog
* Add shared notification

Version 1.0.1, 6 May 2019
-------------------------
* Add draft A1 Mediator API definition
* Use E2 Manager API definition dated 2 May 2019, with tag modifications
* Adjust group IDs and packages for name O-RAN-SC; drop ORAN-OSC
* Add ANR API spec and client code generator
* Update xApp Manager API spec to version 0.1.2

Version 1.0.0, 30 Apr 2019
--------------------------
* Initial version
