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
import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { XMXappInfo, XMDeployableApp, XMDeployedApp } from '../../interfaces/app-mgr.types';
import { InstanceSelectorService } from '../instance-selector/instance-selector.service'
import { RicInstance } from '../../interfaces/dashboard.types';

@Injectable()
export class AppMgrService {

  private basePath: string = '';

  constructor(
    private httpClient: HttpClient,
    private instanceSvc: InstanceSelectorService) {
    this.instanceSvc.getSelectedInstance().subscribe((instance: RicInstance) => {
      console.log("update app mgr path");
      this.basePath = this.instanceSvc.getBasePath()+'/appmgr'
    });
  }



  getDeployable(): Observable<XMDeployableApp[]> {
    console.log("get deployable " + this.basePath)
    return this.httpClient.get<XMDeployableApp[]>(this.basePath + '/xapps/list');
  }

  getDeployed(): Observable<XMDeployedApp[]> {
    return this.httpClient.get<XMDeployedApp[]>(this.basePath + '/xapps');
  }

  deployXapp(name: string): Observable<HttpResponse<Object>> {
    const xappInfo: XMXappInfo = { name: name };
    return this.httpClient.post((this.basePath + '/xapps'), xappInfo, { observe: 'response' });
  }

  undeployXapp(name: string): Observable<HttpResponse<Object>> {
    return this.httpClient.delete((this.basePath + '/xapps'+ '/' + name), { observe: 'response' });
  }

  getConfig(): Observable<any[]>{
    return this.httpClient.get<any[]>("/assets/mockdata/config.json");
    //return this.httpClient.get<any[]>((this.basePath  + '/config'));
  }

  putConfig(config: any): Observable<HttpResponse<Object>> {
    return this.httpClient.put((this.basePath + '/config' ), config, { observe: 'response' });
  }

}
