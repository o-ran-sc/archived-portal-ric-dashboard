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
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { finalize } from 'rxjs/operators';
import { XMDeployableApp } from '../interfaces/app-mgr.types';
import { AppMgrService } from '../services/app-mgr/app-mgr.service';
import { ErrorDialogService } from '../services/ui/error-dialog.service';
import { LoadingDialogService } from '../services/ui/loading-dialog.service';
import { AppConfigurationComponent } from './../app-configuration/app-configuration.component';
import { ConfirmDialogService } from './../services/ui/confirm-dialog.service';
import { NotificationService } from './../services/ui/notification.service';
import { CatalogDataSource } from './catalog.datasource';

@Component({
  selector: 'rd-app-catalog',
  templateUrl: './catalog.component.html',
  styleUrls: ['./catalog.component.scss'],
})
export class CatalogComponent implements OnInit {

  displayedColumns: string[] = ['name', 'version', 'action'];
  dataSource: CatalogDataSource;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    private appMgrService: AppMgrService,
    private confirmDialogService: ConfirmDialogService,
    private dialog: MatDialog,
    private errorDiaglogService: ErrorDialogService,
    private loadingDialogService: LoadingDialogService,
    private notificationService: NotificationService) { }

  ngOnInit() {
    this.dataSource = new CatalogDataSource(this.appMgrService, this.sort, this.notificationService);
    this.dataSource.loadTable();
  }

  onConfigureApp(xapp: XMDeployableApp): void {
    const dialogRef = this.dialog.open(AppConfigurationComponent, {
      width: '40%',
      maxHeight: '500px',
      position: {
        top: '10%'
      },
      data: xapp
    });

  }

  onDeployApp(app: XMDeployableApp): void {
    this.confirmDialogService.openConfirmDialog('Deploy application ' + app.name + '?')
      .afterClosed().subscribe((res: boolean) => {
        if (res) {
          this.loadingDialogService.startLoading('Deploying ' + app.name);
          this.appMgrService.deployXapp(app.name)
            .pipe(
              finalize(() => this.loadingDialogService.stopLoading())
            )
            .subscribe(
              (response: HttpResponse<Object>) => {
                this.notificationService.success('App deploy succeeded!');
              },
              ((her: HttpErrorResponse) => {
                // the error field should have an ErrorTransport object
                let msg = her.message;
                if (her.error && her.error.message) {
                  msg = her.error.message;
                }
                this.notificationService.warn('App deploy failed: ' + msg);
              })
            );
        }
      }
      );
  }

}
