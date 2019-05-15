/*-
 * ========================LICENSE_START=================================
 * O-RAN-SC
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
import { MatDialog} from '@angular/material/dialog';
import { AppRANConnectDialogComponent } from './signal.component.ranconnect-dialog';
import { E2ManagerService } from '../services/e2-mgr/e2-mgr.service';
import { E2SetupRequest } from '../interfaces/e2-mgr.types';

@Component({
  selector: 'app-signal',
  templateUrl: 'signal.component.html',
  styleUrls: ['signal.component.css']
})

export class SignalComponent {
  settings = {
    hideSubHeader: true,
    actions: {
      columnTitle: 'Actions',
      add: false,
      edit: false,
      delete: false,
      position: 'right'
    },
    columns: {
      requestType: {
        title: 'RAN Type',
        type: 'string',
      },
      ranName: {
        title: 'eNodeB/gNodeB Name',
        type: 'string',
      },
      ranIp: {
        title: 'IP',
        type: 'number',
      },
      ranPort: {
        title: 'Port',
        type: 'number',
      },
      responseCode: {
        title: 'Response',
        type: 'number',
      },
      timeStamp: {
        title: 'Time Stamp',
        type: 'string',
      }
    }
  };

  source: LocalDataSource = new LocalDataSource();

  constructor(private service: E2ManagerService, public dialog: MatDialog) { }

  onInit() {
    this.service.getAll().subscribe((val: E2SetupRequest[]) => this.source.load(val));
  }

  openRanConnectDialog() {
    const dialogRef = this.dialog.open(AppRANConnectDialogComponent, {
      width: '450px',
      data: {}
    });
    dialogRef.afterClosed().subscribe(result => {
      this.service.getAll().subscribe((val: any[]) => this.source.load(val));
    });
  }

} // class SignalComponent
