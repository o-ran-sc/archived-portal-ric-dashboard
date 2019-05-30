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
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { XappControlRow } from '../interfaces/xapp-mgr.types';
import { XappMgrService } from '../services/xapp-mgr/xapp-mgr.service';
import { ConfirmDialogService } from './../services/ui/confirm-dialog.service';
import { ErrorDialogService } from './../services/ui/error-dialog.service';
import { NotificationService } from './../services/ui/notification.service';
import { ControlAnimations } from './control.animations';
import { ControlDataSource } from './control.datasource';

@Component({
  selector: 'app-control',
  templateUrl: './control.component.html',
  styleUrls: ['./control.component.css'],
  animations: [ControlAnimations.messageTrigger],
})
export class ControlComponent implements OnInit {

  displayedColumns: string[] = ['xapp', 'name', 'status', 'ip', 'port', 'action'];
  dataSource: ControlDataSource;
  @ViewChild(MatSort) sort: MatSort;

  constructor(
    private xappMgrSvc: XappMgrService,
    private router: Router,
    private confirmDialogService: ConfirmDialogService,
    private errorDialogService: ErrorDialogService,
    private notification: NotificationService) { }

  ngOnInit() {
    this.dataSource = new ControlDataSource(this.xappMgrSvc, this.sort);
    this.dataSource.loadTable();
  }

  controlApp(app: XappControlRow): void {
    const anrXappPattern = /[Aa][Nn][Rr]/;
    if (anrXappPattern.test(app.xapp)) {
      this.router.navigate(['/anr']);
    } else {
      this.errorDialogService.displayError('No control available for ' + app.xapp + ' (yet)');
    }
  }

  undeployApp(app: XappControlRow): void {
    this.confirmDialogService.openConfirmDialog('Are you sure you want to undeploy xApp ' + app.xapp + '?')
      .afterClosed().subscribe(res => {
        if (res) {
          this.xappMgrSvc.undeployXapp(app.xapp).subscribe(
            response => {
              this.dataSource.loadTable();
              switch (response.status) {
                case 200:
                  this.notification.success('xApp undeployed successfully!');
                  break;
                default:
                  this.notification.warn('xApp undeploy failed.');
              }
            }
          );
        }
      });
  }

}
