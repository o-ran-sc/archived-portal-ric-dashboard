/*-
 * ========================LICENSE_START=================================
 * ORAN-OSC
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
export class ControlService {

data = [{
    id: 1,
    xAppName: 'Pendulum Control',
    xAppType: 'Type1',
    podId: 'dc-ric-app-b8c6668d8-56bjb',
    k8Status: 'running',
    age: '25 mins',
  }, {
    id: 2,
    xAppName: 'Dual Connectivity',
    xAppType: 'Type2',
    podId: 'ac-ric-app-5ddbc59ffd-qc6rp',
    k8Status: 'failed',
    age: '5 mins',

  }, {
    id: 3,
    xAppName: 'ANR',
    xAppType: 'Type1',
    podId: 'dc-ric-app-694c45b75f-nqdtt',
    k8Status: 'pending',
    age: '55 mins',
  }, {
    id: 4,
    xAppName: 'Admission Control',
    xAppType: 'Type2',
    podId: 'ac-ric-app-4ddfc59ffd-qc7tp',
    k8Status: 'unkown',
    age: '5 mins',

  }, {
    id: 5,
    xAppName: 'Admission Control',
    xAppType: 'Type2',
    podId: 'ac-ric-app-3ffgc59ffd-qc5rp',
    k8Status: 'crashLoopBackoff',
    age: '5 mins',

  }, {
    id: 6,
    xAppName: 'ANR',
    xAppType: 'Type1',
    podId: 'dc-ric-app-345f44r75f-nertt',
    k8Status: 'completed',
    age: '55 mins',
  }, {
    id: 7,
    xAppName: 'Admission Control',
    xAppType: 'Type2',
    podId: 'ac-ric-app-5ddbc67ffd-qc6rp',
    k8Status: 'completed',
    age: '5 mins',

  }, {
    id: 8,
    xAppName: 'ANR',
    xAppType: 'Type1',
    podId: 'dc-ric-app-694c23b75f-nqdtt',
    k8Status: 'completed',
    age: '55 mins',

  }];

  getData() {
    return this.data; // @TODO implement the service to fetch the backend data
  }
}
