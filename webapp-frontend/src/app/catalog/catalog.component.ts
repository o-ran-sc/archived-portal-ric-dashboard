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
import { Component, OnInit, ViewChild } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { MatSort } from '@angular/material/sort';
import { ErrorDialogService } from '../services/ui/error-dialog.service';
import { AppMgrService } from '../services/app-mgr/app-mgr.service';
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
  @ViewChild(MatSort, {static: true}) sort: MatSort;

  constructor(
    private appMgrService: AppMgrService,
    private confirmDialogService: ConfirmDialogService,
    private errorDiaglogService: ErrorDialogService,
    private notificationService: NotificationService) { }

  ngOnInit() {
    this.dataSource = new CatalogDataSource(this.appMgrService, this.sort, this.notificationService );
    this.dataSource.loadTable();
  }

  onConfigureApp(name: string): void {
    const aboutError = 'Configure not implemented (yet)';
    this.errorDiaglogService.displayError(aboutError);
  }

  onDeployApp(name: string): void {
    this.confirmDialogService.openConfirmDialog('Deploy application ' + name + '?')
      .afterClosed().subscribe( (res: any) => {
        if (res) {
          this.appMgrService.deployXapp(name).subscribe(
            (response: HttpResponse<object>) => {
              this.notificationService.success('Deploy succeeded!');
            },
            (error: HttpErrorResponse) => {
              this.notificationService.warn('Deploy failed: ' + error.message);
            }
          );
        }
      }
    );
  }

}
