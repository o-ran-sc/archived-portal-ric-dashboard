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
import { Component, OnInit} from '@angular/core';
import { XappMgrService } from '../services/xapp-mgr/xapp-mgr.service';
import { ConfirmDialogService } from './../services/ui/confirm-dialog.service'
import { NotificationService } from './../services/ui/notification.service'
import { ErrorDialogService } from '../services/ui/error-dialog.service';
import { CatalogDataSource } from './catalog.datasource';

@Component({
  selector: 'app-catalog',
  templateUrl: './catalog.component.html',
  styleUrls: ['./catalog.component.css'],
})
export class CatalogComponent implements OnInit{

  displayedColumns: string[] = ['name', 'version', 'status', 'action'];
  dataSource: CatalogDataSource;

  constructor(
    private xappMgrSvc: XappMgrService,
    private confirmDialogService: ConfirmDialogService,
    private errorService: ErrorDialogService,
    private notification: NotificationService) { }

  ngOnInit() {
    this.dataSource = new CatalogDataSource(this.xappMgrSvc);
    this.dataSource.loadTable();
  }

  onConfigurexApp(name: string): void {
    const aboutError = 'Not implemented yet';
    this.errorService.displayError(aboutError);
  }

  onDeployxApp(name: string): void {
    this.confirmDialogService.openConfirmDialog('Are you sure you want to deploy this xApp?')
      .afterClosed().subscribe(res => {
        if (res) {
          this.xappMgrSvc.deployXapp(name).subscribe(
            response => {
              switch (response.status) {
                case 200:
                  this.notification.success('xApp deploy succeeded!');
                  break;
                default:
                  this.notification.warn('xApp deploy failed.');
              }
            }
          );
        }
      });

  }
}