# RIC Dashboard Web Application Backend

## Development server

Run `mvn -Dspring.profiles.active=mock spring-boot:run` for a dev server. Navigate to `http://localhost:8080/swagger-ui.html`. 

## Alternate configuration

Run `mvn -Dspring.config.name=application-abc spring-boot:run` to read configuration from the file 'application-abc.properties' in the local directory.

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
