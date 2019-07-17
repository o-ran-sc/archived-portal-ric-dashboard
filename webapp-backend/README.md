# RIC Dashboard Web Application Backend

The RIC Dashboard back-end provides REST services to the Dashboard front-end
Typescript features running in the user's browser.

This back-end uses the ONAP Portal's "EPSDK-FW" library to support single-sign-on
(SSO) when users first authenticate at the ONAP Portal UI.  The SSO feature can
be disabled by running the back-end as a development server, see below.

## Launch regular server

This server requires several well-known properties files on the Java classpath.
These steps are required:

1. Check the set of properties files in the config folder, and create files from
   templates as needed.  E.g., copy "key.properties.template" to "key.properties".
2. Add the config folder to the Java classpath
3a. Launch the server with this command-line invocation:

    java -cp config:target/ric-dash-be-1.2.0-SNAPSHOT.jar \
        -Dloader.main=org.oransc.ric.portal.dashboard.DashboardApplication \
        org.springframework.boot.loader.PropertiesLauncher

3b. To use the configuration in the "application-abc.properties" file, addd a
    key-value pair for "spring.config.name" and launch with an invocation like this:

    java -cp config:target/ric-dash-be-1.2.0-SNAPSHOT.jar \
        -Dspring.config.name=application-abc \
        -Dloader.main=org.oransc.ric.portal.dashboard.DashboardApplication \
        org.springframework.boot.loader.PropertiesLauncher

## Launch development server

A development server uses local configuration and serves mock data that simulates
the behavior of remote endpoints.  Test-area resources provide required property
files. These steps are required:

1. Set an environment variable via JVM argument: "-Dorg.oransc.ric.portal.dashboard=mock"
2. Run the JUnit test case DashboardServerTest (not exactly a classic test)

Launch the server with this command-line invocation:

     mvn -Dorg.oransc.ric.portal.dashboard=mock -Dtest=DashboardTestServer test

## Swagger API documentation

View the server's API documentation at URL `http://localhost:8080/swagger-ui.html`.

## License

Copyright (C) 2019 AT&T Intellectual Property & Nokia. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
