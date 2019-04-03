# ORAN-OSC RIC Dashboard Web Application

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
