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
import { MatDialog } from '@angular/material/dialog';
import { RANConnectionDialogComponent } from './ran-connection-dialog.component';
import { E2ManagerService } from '../services/e2-mgr/e2-mgr.service';
import { ErrorDialogService } from '../services/ui/error-dialog.service';
import { ConfirmDialogService } from './../services/ui/confirm-dialog.service';
import { NotificationService } from './../services/ui/notification.service';
import { RANConnectionDataSource } from './ran-connection.datasource';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-ran-connection',
  templateUrl: './ran-connection.component.html',
  styleUrls: ['./ran-connection.component.scss']
})
export class RANConnectionComponent implements OnInit {
  displayedColumns: string[] = ['requestType', 'ranName', 'ranIp', 'ranPort', 'responseCode', 'timeStamp'];
  dataSource: RANConnectionDataSource;

  constructor(private e2MgrSvc: E2ManagerService,
    private errorSvc: ErrorDialogService,
    private confirmDialogService: ConfirmDialogService,
    private notification: NotificationService,
    public dialog: MatDialog) { }

  ngOnInit() {
    this.dataSource = new RANConnectionDataSource(this.e2MgrSvc);
    this.dataSource.loadTable();
  }

  setupRANConnection() {
    const dialogRef = this.dialog.open(RANConnectionDialogComponent, {
      width: '450px',
      data: {}
    });
    dialogRef.afterClosed().subscribe(result => {
      this.dataSource.loadTable();
    });
  }

  disconnectAllRANConnections() {
    let httpErrRes: HttpErrorResponse;
    const aboutError = 'Disconnect all RAN Connections Failed: ';
    this.confirmDialogService.openConfirmDialog('Are you sure you want to disconnect all RAN connections?')
      .afterClosed().subscribe(res => {
        if (res) {
          this.e2MgrSvc.disconnectAllRAN().subscribe(
            response => {
              if (response.status === 200) {
                this.notification.success('Disconnect all RAN Connections Succeeded!');
                this.dataSource.loadTable();
              }
            },
            (error => {
              httpErrRes = error;
              this.errorSvc.displayError(aboutError + httpErrRes.message);
            })
          );
        }
      });
  }
}
