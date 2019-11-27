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

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { V1PodList } from '@kubernetes/client-node';
import { Observable } from 'rxjs';
import { CommonService } from '../common/common.service';

/**
* Services for calling the Dashboard's caas-ingress endpoints to get Kubernetes details.
*/
@Injectable({
  providedIn: 'root'
})
export class CaasIngressService {

  private component = 'caas-ingress';
  private podsPath = 'pods';

  constructor(
    private httpClient: HttpClient,
    private commonSvc: CommonService) {
    // injects to variable httpClient
  }

  /**
   * Gets list of pods
   * @returns Observable that should yield a V1PodList
   */
  getPodList(instanceKey: string, cluster: string, namespace: string): Observable<V1PodList> {
    const url = this.commonSvc.buildPath(instanceKey, this.component, 'pods', 'cluster', cluster, 'namespace', namespace);
    return this.httpClient.get<V1PodList>(url);
  }

}
