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
import { Component, OnInit } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { ControlService } from '../services/control/control.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-control',
  templateUrl: './control.component.html',
  styleUrls: ['./control.component.css']
})
export class ControlComponent {

    settings = {
    hideSubHeader: true,
    actions: {
      columnTitle: 'Actions',
      add: false,
      edit: false,
      delete: false,
      custom: [
      { name: 'view', title: 'view', },
    ],
      position: 'right'

    },
    columns: {
      id: {
        title: 'ID',
        type: 'number',
      },
      xAppName: {
        title: 'xApp Name',
        type: 'string',
      },
      xAppType: {
        title: 'xApp Type',
        type: 'string',
      },
      podId: {
        title: 'Pod ID',
        type: 'number',
      },
      k8Status: {
        title: 'k8 Status',
        type: 'string',
      },
      age: {
        title: 'Age',
        type: 'string',
      },
    },
  };

  source: LocalDataSource = new LocalDataSource();

  constructor(private service: ControlService, private router: Router) {
    const data = this.service.getData();
    this.source.load(data);
  }

  view(event): void {
      const url = '/xapp';
      this.router.navigate([url, event]).then( (e) => {
            if (e) {
                console.log(event.data);
                console.log('Navigation is successful!');
            } else {
                console.log('Navigation has failed!');
            }
        });
  }


}
