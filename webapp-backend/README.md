# RIC Dashboard Web Application Backend

The RIC Dashboard back-end provides REST services to the Dashboard
front-end Typescript features running in the user's browser.  For
production use, this server also offers the Angular application files.

The server uses the ONAP Portal's "EPSDK-FW" library to support
single-sign-on (SSO) feature, which requires users to authenticate
at the ONAP Portal UI.  Authentication features including SSO are
excluded by Spring profiles when running the back-end as a development
server, see below.

## Launch production server

This server requires several configuration files:

    application.properties - read from filesystem
    key.properties - read from Java classpath
    portal.properties - read from Java classpath

All files should be placed in a "config" directory.  That name is important;
Spring automatically searches that directory for the application.properties file.
Further, that directory is placed on the Java classpath so the additional
files can be read at runtime.

These steps are required:

1. Check the set of properties files in the config folder, and create
   files from templates as needed.  E.g., copy
   "key.properties.template" to "key.properties".
2. Add the config folder to the Java classpath
3a. Launch the server with this command-line invocation that includes the
   config directory on the Java classpath:

    java -cp config:target/ric-dash-be-1.2.0-SNAPSHOT.jar \
        -Dloader.main=org.oransc.ric.portal.dashboard.DashboardApplication \
        org.springframework.boot.loader.PropertiesLauncher

3b. Alternately, to use the configuration in the "application-abc.properties" file,
add a key-value pair for "spring.config.name" and launch with this invocation:

    java -cp config:target/ric-dash-be-1.2.0-SNAPSHOT.jar \
        -Dspring.config.name=application-abc \
        -Dloader.main=org.oransc.ric.portal.dashboard.DashboardApplication \
        org.springframework.boot.loader.PropertiesLauncher

### Production user authentication

TODO AUTH: Authentication temporarily disabled for Sprint 2 testing!

The regular server authenticates requests using cookies that are set
by the ONAP Portal:

     EPService
     UserId

The EPService value is not checked.  The UserId value is decrypted
using a secret key shared with the ONAP Portal to yield a user ID.
That ID must match a user's loginId defined in the user manager.

The regular server checks requests for the following granted
authorities (role names), as defined in the DashboardConstants class.
A standard user can read (GET) all methods but not make changes.
An administrator can read (GET) and write (POST PUT DELETE) all data.

    Standard_User
	System_Administrator

Use the following structure in a JSON file to publish a user for the
user manager:

    [
     {
      "orgId":null,
      "managerId":null,
      "firstName":"Demo",
      "middleInitial":null,
      "lastName":"User",
      "phone":null,
      "email":null,
      "hrid":null,
      "orgUserId":null,
      "orgCode":null,
      "orgManagerUserId":null,
      "jobTitle":null,
      "loginId":"demo",
      "active":true,
      "roles":[
         {
            "id":null,
            "name":"Standard_User",
            "roleFunctions":null
         }
      ]
     }
    ]


## Launch development server

The development server uses local configuration and serves mock data
that simulates the behavior of remote endpoints.  The directory
src/main/resources contains usable versions of the required property
files.  These steps are required to launch:

1. Set an environment variable via JVM argument: "-Dorg.oransc.ric.portal.dashboard=mock"
2. Run the JUnit test case DashboardServerTest -- not exactly a "test" because it never finishes.

Both steps can be done with this command-line invocation:

     mvn -Dorg.oransc.ric.portal.dashboard=mock -Dtest=DashboardTestServer test

### Development user authentication

The development server requires basic HTTP user authentication for all requests. Like
the production server, it requires HTTP headers with authentication for Portal API
requests.  The credentials are in constants in this Java class in the src/test/java
folder:

    org.oransc.ric.portal.dashboard.config.WebSecurityMockConfiguration

Like the production server, the development server also performs role-based
authentication on requests. The user name-role  name associations are defined
in the class shown above.

## Swagger API documentation

Both a regular and a development server publish API documentation at URL `http://localhost:8080/swagger-ui.html`.

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
