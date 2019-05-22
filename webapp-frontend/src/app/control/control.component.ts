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
import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { XappMgrService } from '../services/xapp-mgr/xapp-mgr.service';
import { Router } from '@angular/router';
import { ConfirmDialogService } from './../services/ui/confirm-dialog.service'
import { NotificationService } from './../services/ui/notification.service'
import { XMXapp } from '../interfaces/xapp-mgr.types';


@Component({
  selector: 'app-control',
  templateUrl: './control.component.html',
  styleUrls: ['./control.component.css'],
  encapsulation: ViewEncapsulation.Emulated,
})
export class ControlComponent implements OnInit {



  displayedColumns: string[] = ['xapp', 'name', 'status', 'ip', 'port', 'txMessages','rxMessages','action'];
  dataSource: any;

  constructor(
    private xappMgrSvc: XappMgrService,
    private router: Router,
    private confirmDialogService: ConfirmDialogService,
    private notification: NotificationService) { }

  ngOnInit() {
    this.xappMgrSvc.getAll().subscribe((xapps: XMXapp[]) => this.dataSource = this.getInstance(xapps));
  }

  view(): void {
    const url = '/xapp';
    this.router.navigate([url]);
  }

  undeploy(name: string): void {
    this.confirmDialogService.openConfirmDialog('Are you sure you want to undeploy this xApp ?')
      .afterClosed().subscribe(res => {
        if (res) {
          this.xappMgrSvc.undeployXapp(name).subscribe(
            response => {
              this.xappMgrSvc.getAll().subscribe((xapps: XMXapp[]) => this.dataSource = this.getInstance(xapps));
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

  getInstance(allxappdata: XMXapp[]) {
    const xAppInstances = [];
    for (const xappindex in allxappdata) {
      const instancelist = allxappdata[xappindex].instances;
      for (const instanceindex in instancelist) {
        var instance: any;
        instance = instancelist[instanceindex];
        instance.xapp = allxappdata[xappindex].name;
        xAppInstances.push(instance);
      }
    }
    return xAppInstances;
  }

}
