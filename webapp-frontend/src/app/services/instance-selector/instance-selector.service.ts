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
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class InstanceSelectorService {

  selectedInstance: BehaviorSubject<string> = new BehaviorSubject<string>("");

  //need remove the test data
  testinstancelist = [
    "test RIC1",
    "test RIC2",
    "test RIC3",
  ]

  constructor(private httpClient: HttpClient) { }

  private basePath = 'api/instance';

  getInstanceList() {
    return this.testinstancelist;
  }

  getSelectedInstance():Observable<string> {
    return this.selectedInstance;
  }

  selectInstance(instance: string) {
    this.selectedInstance = new BehaviorSubject<string>(instance);
    console.log(this.selectedInstance)
  }
}
