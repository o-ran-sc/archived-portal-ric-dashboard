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
import { SignalService } from '../services/signal/signal.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signal',
  templateUrl: './signal.component.html',
  styleUrls: ['./signal.component.css']
})
export class SignalComponent {

    settings = {
    hideSubHeader: true,
    actions: {
      columnTitle: 'Actions',
      add: true,
      edit: false,
      delete: true,
      custom: [
      { name: 'Setup', title: 'Setup'},
    ],
      position: 'right'

    },
    columns: {
      
      xAppName: {
        title: 'eNodeB/gNodeB',
        type: 'string',
      },
      xAppType: {
        title: 'IP',
        type: 'number',
      },
      podId: {
        title: 'Port',
        type: 'number',
      },
      k8Status: {
        title: 'Status',
        type: 'string',
      }
      
    },
  };

/*  source: LocalDataSource = new LocalDataSource();

  constructor(private service: SignalService, private router: Router) {
    const data = this.service.getData();
    this.source.load(data);
  }*/

  source: LocalDataSource = new LocalDataSource();

  constructor(private service: SignalService) {
    this.service.getAll().subscribe((val:any[]) => this.source.load(val));
  }

/*  view(event): void {
      const url = '/xapp';
      this.router.navigate([url, event]).then( (e) => {
            if (e) {
                console.log(event.data);
                console.log('Navigation is successful!');
            } else {
                console.log('Navigation has failed!');
            }
        });
  } */


}
