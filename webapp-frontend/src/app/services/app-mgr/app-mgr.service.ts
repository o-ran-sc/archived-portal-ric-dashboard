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
import { XMDeployableApp, XMDeployedApp, XMXappInfo } from '../../interfaces/app-mgr.types';

@Injectable()
export class AppMgrService {

  private component = 'appmgr';

  constructor(
    private httpClient: HttpClient) {
  }

  private buildPath(instanceKey: string, ...args: any[]) {
    let result = 'api/' + this.component + '/ric/' + instanceKey;
    args.forEach(part => {
      result = result + '/' + part;
    });
    return result;
  }

  getDeployable(instanceKey: string): Observable<XMDeployableApp[]> {
    const url = this.buildPath(instanceKey, 'xapps', 'list');
    return this.httpClient.get<XMDeployableApp[]>(url);
  }

  getDeployed(instanceKey: string): Observable<XMDeployedApp[]> {
    const url = this.buildPath(instanceKey, 'xapps');
    return this.httpClient.get<XMDeployedApp[]>(url);
  }

  deployXapp(instanceKey: string, name: string): Observable<HttpResponse<Object>> {
    const xappInfo: XMXappInfo = { name: name };
    const url = this.buildPath(instanceKey, 'xapps');
    return this.httpClient.post(url, xappInfo, { observe: 'response' });
  }

  undeployXapp(instanceKey: string, name: string): Observable<HttpResponse<Object>> {
    const url = this.buildPath(instanceKey, 'xapps', name);
    return this.httpClient.delete(url, { observe: 'response' });
  }

  getConfig(): Observable<any[]> {
    return this.httpClient.get<any[]>("/assets/mockdata/config.json");
    //const url = this.buildPath(instanceKey, 'config');
    //return this.httpClient.get<any[]>(url);
  }

  putConfig(instanceKey: string, config: any): Observable<HttpResponse<Object>> {
    const url = this.buildPath(instanceKey, 'config');
    return this.httpClient.put(url, config, { observe: 'response' });
  }

}
