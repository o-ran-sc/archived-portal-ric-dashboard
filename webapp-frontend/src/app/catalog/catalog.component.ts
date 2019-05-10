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
import { Component, Inject } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { CatalogService } from '../services/catalog/catalog.service';
import { ConfirmDialogService } from './../services/ui/confirm-dialog.service'
import { NotificationService } from './../services/ui/notification.service'

@Component({
  selector: 'app-catalog',
  templateUrl: './catalog.component.html',
  styleUrls: ['./catalog.component.css']
})
export class CatalogComponent {

  settings = {
    hideSubHeader: true,
    actions: {
      columnTitle: 'Actions',
      add: false,
      edit: false,
      delete: false,
      custom: [
        { name: 'deployxApp', title: 'Deploy' },
      ],
      position: 'right'

    },
    columns: {
      name: {
        title: 'xApp Name',
        type: 'string',
      },
      version: {
        title: 'xApp Version',
        type: 'string',
      },
      status: {
        title: 'Status',
        type: 'string',
      },
    },
  };

  source: LocalDataSource = new LocalDataSource();

  constructor(
    private service: CatalogService,
    private confirmDialogService: ConfirmDialogService,
    private notification: NotificationService) {
    this.service.getAll().subscribe((val: any[]) => this.source.load(val));
  }

  onDeployxApp(event): void {
    this.confirmDialogService.openConfirmDialog('Are you sure you want to deploy this xApp?')
      .afterClosed().subscribe(res => {
        if (res) {
          this.service.deployXapp(event.data.name).subscribe(
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