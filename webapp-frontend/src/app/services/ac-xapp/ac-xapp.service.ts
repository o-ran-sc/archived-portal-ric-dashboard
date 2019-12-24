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
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ACAdmissionIntervalControl, ACAdmissionIntervalControlAck } from '../../interfaces/ac-xapp.types';
import { DashboardSuccessTransport } from '../../interfaces/dashboard.types';
import { DashboardService } from '../dashboard/dashboard.service';

/**
 * Services for calling the Dashboard's A1 endpoints to get/put AC policies.
 */
@Injectable({
  providedIn: 'root'
})
export class ACXappService {

  private component = 'a1-p';
  private policyPath = 'policies';
  private acPolicyName = 'admission_control_policy';

  constructor(
    private dashboardSvc: DashboardService,
    private httpClient: HttpClient) {
  }

  /**
   * Gets AC client version details
   * @returns Observable that yields a String
   */
  getVersion(instanceKey: string): Observable<string> {
    const path = this.dashboardSvc.buildPath(this.component, null, 'version');
    return this.httpClient.get<DashboardSuccessTransport>(path).pipe(
      // Extract the string here
      map(res => res['data'])
    );
  }

  /**
   * Gets admission control policy.
   * @returns Observable that yields an ACAdmissionIntervalControl
   */
  getPolicy(instanceKey: string): Observable<ACAdmissionIntervalControl> {
    const path = this.dashboardSvc.buildPath(this.component, instanceKey, this.policyPath, this.acPolicyName);
    return this.httpClient.get<ACAdmissionIntervalControl>(path);
  }

  /**
   * Puts admission control policy.
   * @param policy an instance of ACAdmissionIntervalControl
   * @returns Observable that yields a response code, no data
   */
  putPolicy(instanceKey: string, policy: ACAdmissionIntervalControl): Observable<any> {
    const path = this.dashboardSvc.buildPath(this.component, instanceKey, this.policyPath, this.acPolicyName);
    return this.httpClient.put<ACAdmissionIntervalControlAck>(path, policy, { observe: 'response' });
  }

}
