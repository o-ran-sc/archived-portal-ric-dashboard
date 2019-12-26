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
import { CommonService } from '../common/common.service';

/**
 * Services for calling the Dashboard's A1 endpoints to get/put AC policies.
 */
@Injectable({
  providedIn: 'root'
})
export class ACXappService {

  private component = 'a1-p';
  private policyTypePath = 'poltype';
  private policyInstPath = 'polinst';
  private acPolicyType = '1';
  private acPolicyInst = 'admission_control_policy';

  constructor(
    private httpClient: HttpClient,
    private commonSvc: CommonService) {
    // injects to variable httpClient
  }

  /**
   * Gets version details
   * @returns Observable that should yield a String
   */
  getVersion(): Observable<string> {
    const url = 'api/a1-p/version'
    return this.httpClient.get<DashboardSuccessTransport>(url).pipe(
      // Extract the string here
      map(res => res['data'])
    );
  }

  /**
   * Gets admission control policy.
   * @returns Observable that should yield an ACAdmissionIntervalControl
   */
  getPolicy(instanceKey: string): Observable<ACAdmissionIntervalControl> {
    const url = this.commonSvc.buildPath(instanceKey, this.component, this.policyTypePath, this.acPolicyType,
      this.policyInstPath, this.acPolicyInst);
    return this.httpClient.get<ACAdmissionIntervalControl>(url);
  }

  /**
   * Puts admission control policy.
   * @param policy an instance of ACAdmissionIntervalControl
   * @returns Observable that should yield a response code, no data
   */
  putPolicy(instanceKey: string, policy: ACAdmissionIntervalControl): Observable<any> {
    const url = this.commonSvc.buildPath(instanceKey, this.component, this.policyTypePath, this.acPolicyType,
      this.policyInstPath, this.acPolicyInst);
    return this.httpClient.put<ACAdmissionIntervalControlAck>(url, policy, { observe: 'response' });
  }

}
