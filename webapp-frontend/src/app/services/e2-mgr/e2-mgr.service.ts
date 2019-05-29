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
import { HttpClient } from '@angular/common/http';
import { E2SetupRequest } from '../../interfaces/e2-mgr.types';
import { Observable } from 'rxjs';

@Injectable()
export class E2ManagerService {

  private basePath = 'api/e2mgr/';

  constructor(private httpClient: HttpClient) {
    // injects to variable httpClient
  }

  /**
   * Gets E2 manager client version details
   * @returns Observable that should yield a DashboardSuccessTransport object
   */
  getE2ManagerVersion() {
    return this.httpClient.get(this.basePath + 'version');
  }

  /**
   * Gets setup request history
   * @returns Observable that should yield an array of objects
   */
  getAll() {
    return this.httpClient.get(this.basePath + 'setup');
  }

  /**
   * Sends a request to setup an ENDC/gNodeB connection
   * @returns Observable
   */
  endcSetup(req: E2SetupRequest) {
    return this.httpClient.post(this.basePath + 'endcSetup', req);
  }

  /**
   * Sends a request to setup an X2/eNodeB connection
   * @returns Observable
   */
  x2Setup(req: E2SetupRequest) {
    return this.httpClient.post(this.basePath + 'x2Setup', req);
  }

  disconnectAllRAN() {
    return this.httpClient.delete((this.basePath + 'disconnectAllRAN'), { observe: 'response' });
  }

}
