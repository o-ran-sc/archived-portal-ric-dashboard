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

import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { CaasIngressDataSource } from './caas-ingress.datasource';
import { CaasIngressService } from '../services/caas-ingress/caas-ingress.service';
import { ConfirmDialogService } from '../services/ui/confirm-dialog.service';
import { ErrorDialogService } from '../services/ui/error-dialog.service';
import { LoadingDialogService } from '../services/ui/loading-dialog.service';
import { NotificationService } from '../services/ui/notification.service';

@Component({
  selector: 'rd-caas-ingress',
  templateUrl: './caas-ingress.component.html',
  styleUrls: ['./caas-ingress.component.scss']
})
export class CaasIngressComponent implements OnInit {

  // Must supply namespace when instantiating!
  @Input() namespace: string;

  dataSource: CaasIngressDataSource;
  displayedColumns: string[] = ['name', 'status', 'ip', 'startTime' ];
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    private caasIngressSvc: CaasIngressService,
    private confirmDialogService: ConfirmDialogService,
    private errorDialogService: ErrorDialogService,
    private loadingDialogService: LoadingDialogService,
    private notificationService: NotificationService) { }

  ngOnInit() {
    this.dataSource = new CaasIngressDataSource(this.caasIngressSvc, this.sort, this.notificationService);
    this.dataSource.loadTable(this.namespace);
  }

}
