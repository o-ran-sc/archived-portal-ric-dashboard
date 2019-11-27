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
import { CommonService } from '../common/common.service';

@Injectable()
export class AppMgrService {

  private component = 'appmgr';

  constructor(
    private httpClient: HttpClient,
    private commonSvc: CommonService) {
  }

  getDeployable(instanceKey: string): Observable<XMDeployableApp[]> {
    const url = this.commonSvc.buildPath(instanceKey, this.component, 'xapps', 'list');
    return this.httpClient.get<XMDeployableApp[]>(url);
  }

  getDeployed(instanceKey: string): Observable<XMDeployedApp[]> {
    const url = this.commonSvc.buildPath(instanceKey, this.component, 'xapps');
    return this.httpClient.get<XMDeployedApp[]>(url);
  }

  deployXapp(instanceKey: string, name: string): Observable<HttpResponse<Object>> {
    const xappInfo: XMXappInfo = { name: name };
    const url = this.commonSvc.buildPath(instanceKey, this.component, 'xapps');
    return this.httpClient.post(url, xappInfo, { observe: 'response' });
  }

  undeployXapp(instanceKey: string, name: string): Observable<HttpResponse<Object>> {
    const url = this.commonSvc.buildPath(instanceKey, this.component, 'xapps', name);
    return this.httpClient.delete(url, { observe: 'response' });
  }

  getConfig(instanceKey: string): Observable<any[]> {
    // For demo purpose, pull example config from local
    return this.httpClient.get<any[]>("/assets/mockdata/config.json");
    // Once Xapp manager contains layout, should call backend to get xapp config 
    //const url = this.commonSvc.buildPath(instanceKey, this.component, 'config');
    //return this.httpClient.get<any[]>(url);
  }

  putConfig(instanceKey: string, config: any): Observable<HttpResponse<Object>> {
    const url = this.commonSvc.buildPath(instanceKey, this.component, 'config');
    return this.httpClient.put(url, config, { observe: 'response' });
  }

}
