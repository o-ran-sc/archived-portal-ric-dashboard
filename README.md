# O-RAN-SC RIC Dashboard Web Application

This webapp is built with Angular 7 and Spring-Boot 2.

## Getting started

To install prerequisites on Mac OSX, first install nvm then continue with node:

	curl -o- https://raw.githubusercontent.com/creationix/nvm/v0.33.0/install.sh | bash

Then:

	nvm install stable
	nvm install node

To run the web app:

    cd ric-dashboard/ang7-sb2
    mvn clean install

    cd ric-dashboard/ang7-sb2/backend/
    mvn spring-boot:run

To debug the frontend and backend for Angular developers:

    cd ric-dashboard/ang7-sb2/frontend/src/main/web/src/app
    ./ng serve --proxy-config proxy.conf.json

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
