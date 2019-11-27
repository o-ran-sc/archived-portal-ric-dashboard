/*-
 * ========================LICENSE_START=================================
 * O-RAN-SC
 * %%
 * Copyright (C) 2019 AT&T Intellectual Property
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
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { DashboardSuccessTransport } from '../../interfaces/dashboard.types';
import { E2RanDetails, E2SetupRequest } from '../../interfaces/e2-mgr.types';
import { InstanceSelectorService } from '../instance-selector/instance-selector.service';


@Injectable({
  providedIn: 'root'
})

export class E2ManagerService {

  private component = 'e2mgr';

  constructor(
    private httpClient: HttpClient,
    private instanceSelectorService: InstanceSelectorService) {
    // injects to variable httpClient
  }

  /**
   * Gets E2 client version details
   * @returns Observable that should yield a String
   */
  getVersion(): Observable<string> {
    return this.httpClient.get<DashboardSuccessTransport>(this.instanceSelectorService.getApiBasePath(this.component) + 'version').pipe(
      // Extract the string here
      map(res => res['data'])
    );
  }

  /**
   * Gets RAN details
   * @returns Observable that should yield an array of objects
   */
  getRan(): Observable<Array<E2RanDetails>> {
    return this.httpClient.get<Array<E2RanDetails>>(this.instanceSelectorService.getApiBasePath(this.component) + '/nodeb/ran');
  }

  /**
   * Sends a request to setup an ENDC/gNodeB connection
   * @returns Observable. On success there is no data, only a code.
   */
  endcSetup(req: E2SetupRequest): Observable<HttpResponse<Object>> {
    return this.httpClient.post(this.instanceSelectorService.getApiBasePath(this.component) + '/nodeb/endc-setup', req, { observe: 'response' });
  }

  /**
   * Sends a request to setup an X2/eNodeB connection
   * @returns Observable. On success there is no data, only a code.
   */
  x2Setup(req: E2SetupRequest): Observable<HttpResponse<Object>> {
    return this.httpClient.post(this.instanceSelectorService.getApiBasePath(this.component) + '/nodeb/x2-setup', req, { observe: 'response' });
  }

  /**
   * Sends a request to drop all RAN connections
   * @returns Observable with body.
   */
  nodebPut(): Observable<any> {
    return this.httpClient.put((this.instanceSelectorService.getApiBasePath(this.component) + '/nodeb/shutdown'), { observe: 'body' });
  }

}
