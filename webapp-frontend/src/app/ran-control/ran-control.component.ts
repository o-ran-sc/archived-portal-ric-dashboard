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
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { RanControlConnectDialogComponent } from './ran-connection-dialog.component';
import { E2ManagerService } from '../services/e2-mgr/e2-mgr.service';
import { ErrorDialogService } from '../services/ui/error-dialog.service';
import { ConfirmDialogService } from '../services/ui/confirm-dialog.service';
import { NotificationService } from '../services/ui/notification.service';
import { RANControlDataSource } from './ran-control.datasource';

@Component({
  selector: 'rd-ran-control',
  templateUrl: './ran-control.component.html',
  styleUrls: ['./ran-control.component.scss']
})
export class RanControlComponent implements OnInit {
  displayedColumns: string[] = ['nbId', 'nodeType', 'ranName', 'ranIp', 'ranPort', 'connectionStatus'];
  dataSource: RANControlDataSource;

  constructor(private e2MgrSvc: E2ManagerService,
    private errorDialogService: ErrorDialogService,
    private confirmDialogService: ConfirmDialogService,
    private notificationService: NotificationService,
    public dialog: MatDialog) { }

  ngOnInit() {
    this.dataSource = new RANControlDataSource(this.e2MgrSvc, this.notificationService);
    this.dataSource.loadTable();
  }

  setupRANConnection() {
    const dialogRef = this.dialog.open(RanControlConnectDialogComponent, {
      width: '450px',
      data: {}
    });
    dialogRef.afterClosed().subscribe( (result: boolean) => {
      if (result) {
        this.dataSource.loadTable();
      }
    });
  }

  disconnectAllRANConnections() {
    const aboutError = 'Disconnect all RAN Connections Failed: ';
    this.confirmDialogService.openConfirmDialog('Are you sure you want to disconnect all RAN connections?')
      .afterClosed().subscribe( (res: boolean) => {
        if (res) {
          this.e2MgrSvc.nodebDelete().subscribe(
            ( response: HttpResponse<Object>) => {
              if (response.status === 200) {
                this.notificationService.success('Disconnect all RAN Connections Succeeded!');
                this.dataSource.loadTable();
              }
            },
            ( (error: HttpErrorResponse) => {
              this.errorDialogService.displayError(aboutError + error.message);
            })
          );
        }
      });
  }

}
