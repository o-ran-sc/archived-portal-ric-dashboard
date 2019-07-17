# O-RAN-SC RIC Dashboard Web Application

The O-RAN SC RIC Dashboard provides administrative and operator functions
for a disaggregated radio access network (RAN) controller.
The web app is built as a single-page app using an Angular 8 front end
and a Spring-Boot 2 back end.

## Deployment configuration

The application expects the following configuration files,
usually mounted as files from Kubernetes configuration maps:

    application.properties (in launch directory)
    key.properties (on classpath)
    portal.properties (on classpath)

Sample files are in directory src/main/resources and src/test/resources.

## Development guide

This section gives a quickstart guide for developers.

### Prerequisites

1. Java development kit (JDK), version 11 or later
2. Maven dependency-management tool, version 3.4 or later

### Build and launch the web app

Instructions for launching a backend Sprint-Boot server
are available in the webapp-backend README file.
After launching, open a browser on http://localhost:8080

Instructions for launching a frontend Angular server (only for development)
are available in the webapp-frontend README file.
After launching, open a browser on http://localhost:4200

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
