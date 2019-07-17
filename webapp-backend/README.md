# RIC Dashboard Web Application Backend

## Launch server

Run `mvn -Dspring.config.name=application-abc spring-boot:run` to run a server configured
by the file 'application-abc.properties' in the local directory.

## Development server

This back-end project can be configured to use local configuration and serve mock data
that simulates the behavior of remote endpoints.  These steps are required:

1. Check the set of properties files in the config folder, and create files from
   templates as needed.  For example, copy "key.properties.template" to "key.properties".
2. Add the "config" folder to the Java classpath
3. Set an environment variable via JVM argument: "-Dorg.oransc.ric.portal.dashboard=mock"
4. Run the JUnit test case DashboardServerTest

All but the first step can be done with suitable configuration in Eclipse, or via the
following command-line invocation:

    java -cp config:target/ric-dash-be-1.2.0-SNAPSHOT.jar \
        -Dloader.main=org.oransc.ric.portal.dashboard.DashboardApplication \
        org.springframework.boot.loader.PropertiesLauncher

If you need to use the configuration in the "application-abc.properties" file, add
a "spring.config.name" key=value pair. Then the previous command becomes:

    java -cp config:target/ric-dash-be-1.2.0-SNAPSHOT.jar \
        -Dspring.config.name=application-abc \
        -Dloader.main=org.oransc.ric.portal.dashboard.DashboardApplication \
        org.springframework.boot.loader.PropertiesLauncher

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
