/*-
 * ========================LICENSE_START=================================
 * O-RAN-SC
 * %%
 * Copyright (C) 2019 AT&T Intellectual Property and Nokia
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================LICENSE_END===================================
 */
import { Injectable } from '@angular/core';

@Injectable()
export class AdminService {

data = [{
    id: 1,
    firstName: 'John',
    lastName: 'Doe',
    status: 'Active',
  }, {
    id: 2,
    firstName: 'Alice',
    lastName: 'Nolan',
    status: 'Active',
  }, {
    id: 3,
    firstName: 'Pierce',
    lastName: 'King',
    status: 'InActive',
  }, {
    id: 4,
    firstName: 'Paul',
    lastName: 'Smith',
    status: 'InActive',
  }, {
    id: 5,
    firstName: 'Jack',
    lastName: 'Reacher',
    status: 'Active',
  }];

  getData() {
    return this.data; // @TODO implement the service to fetch the backend data
  }
}
