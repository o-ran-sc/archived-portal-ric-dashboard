# O-RAN-SC RIC Dashboard Web Application

This webapp is built with Angular 7 and Spring-Boot 2.

## Deployment configuration

The application expects a JSON-formatted configuration in an
environment variable SPRING_APPLICATION_JSON with the following:

    {
      "a1med": {
        "url": "http://1.2.3.4:56"
      },
      "anrxapp": {
        "url" : "http://1.2.3.4:56"
      },
      "e2mgr": {
        "url": "http://1.2.3.4:56"
      },
      "xappmgr": {
        "url": "http://1.2.3.4:56"
      }
    }

## Development guide

This section gives a quickstart guide for developers.

### Check prerequisites

1. Java development kit (JDK), version 1.8 or later
2. Maven dependency-management tool, version 3.4 or later

### Build and launch the web app

    mvn -Ddocker.skip=true clean install
    cd webapp-backend
    mvn spring-boot:run

Then open a browser on http://localhost:8080

In addition to the above, you can run the Angular server
for debugging the frontend and backend separately:

    cd webapp-frontend
    ./ng serve --proxy-config proxy.conf.json

Then open a browser on http://localhost:4200

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
