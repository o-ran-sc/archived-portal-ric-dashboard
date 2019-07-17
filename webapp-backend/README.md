# RIC Dashboard Web Application Backend

## Launch server

Run `mvn -Dspring.config.name=application-abc spring-boot:run` to run a server configured
by the file 'application-abc.properties' in the local directory.

## Development server

Set an environment variable via JVM argument "-Dorg.oransc.ric.portal.dashboard=mock"
and run the JUnit test case DashboardServerTest for a development server to run standalone
with mock configuration and data that simulates the behavior of remote endpoints.

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
