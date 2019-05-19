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
import { LocalDataSource } from 'ng2-smart-table';
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
export class ControlComponent {

  settings = {
    hideSubHeader: true,
    actions: {
      columnTitle: 'Actions',
      add: false,
      edit: false,
      delete: false,
      custom: [
        { name: 'view', title: '<i class="material-icons">visibility</i>', },
        { name: 'undeploy', title: '<i class="material-icons red-close">close</i>', },
      ],
      position: 'right'

    },
    columns: {
      xapp: {
        title: 'xApp Name',
        type: 'string',
      },
      name: {
        title: 'Instance Name',
        type: 'string',
      },
      status: {
        title: 'Status',
        type: 'string',
      },
      ip: {
        title: 'IP',
        type: 'string',
      },
      port: {
        title: 'Port',
        type: 'integer',
      },
      txMessages: {
        title: 'txMessages',
        type: 'array',
      },
      rxMessages: {
        title: 'rxMessages',
        type: 'array',
      },
    },
  };

  source: LocalDataSource = new LocalDataSource();

  constructor(
    private xappMgr: XappMgrService,
    private router: Router,
    private confirmDialogService: ConfirmDialogService,
    private notification: NotificationService) {
    this.xappMgr.getAll().subscribe((xapps: XMXapp[]) => this.source.load(this.getInstance(xapps)));
  }

  onxAppControlAction(event) {
    switch (event.action) {
      case 'view':
        this.view(event);
        break;
      case 'undeploy':
        this.undeploy(event);
        break;
    }
  }

  view(event): void {
    const url = '/xapp';
    this.router.navigate([url, event]);
  }

  undeploy(event): void {
    this.confirmDialogService.openConfirmDialog('Are you sure you want to undeploy this xApp ?')
      .afterClosed().subscribe(res => {
        if (res) {
          this.xappMgr.undeployXapp(event.data.xapp).subscribe(
            response => {
              this.xappMgr.getAll().subscribe((xapps: XMXapp[]) => this.source.load(this.getInstance(xapps)));
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

  getInstance(allxappdata) {
    const xAppInstances = [];
    for (const xappindex in allxappdata) {
      const instancelist = allxappdata[xappindex].instances;
      for (const instanceindex in instancelist) {
        const instance = instancelist[instanceindex];
        instance.xapp = allxappdata[xappindex].name;
        xAppInstances.push(instance);
      }
    }
    return xAppInstances;
  }


}
