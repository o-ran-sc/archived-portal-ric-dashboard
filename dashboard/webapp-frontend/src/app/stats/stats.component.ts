/*-
 * ========================LICENSE_START=================================
 * O-RAN-SC
 * %%
 * Copyright (C) 2019 AT&T Intellectual Property
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
import { Component, OnInit, ViewChildren, QueryList } from '@angular/core';
import { BaseChartDirective } from 'ng2-charts/ng2-charts';
import { StatsService } from '../services/stats/stats.service';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { DashboardSuccessTransport } from '../interfaces/dashboard.types';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { MatDialog, MatTabChangeEvent } from '@angular/material';
import { ConfirmDialogService } from '../services/ui/confirm-dialog.service';
import { ErrorDialogService } from '../services/ui/error-dialog.service';
import { LoadingDialogService } from '../services/ui/loading-dialog.service';
import { NotificationService } from '../services/ui/notification.service';
import { UiService } from '../services/ui/ui.service';
import { InstanceSelectorService } from '../services/instance-selector/instance-selector.service';
import { StatsDataSource } from './stats-datasource';
import { Subscription, BehaviorSubject } from 'rxjs';
import { RANControlDataSource } from '../ran-control/ran-control.datasource';
import { RanControlConnectDialogComponent } from '../ran-control/ran-connection-dialog.component';
import { StatsDialogComponent } from './stats-dialog.component';
import { StatsDetails } from '../interfaces/e2-mgr.types';
import {FormControl} from '@angular/forms';
import { RicInstance} from '../interfaces/dashboard.types';

@Component({
    selector: 'rd-stats',
    templateUrl: './stats.component.html',
    styleUrls: ['./stats.component.scss']
})
export class StatsComponent implements OnInit {

    @ViewChildren(BaseChartDirective) charts: QueryList<BaseChartDirective>;
    checked = false;
    darkMode: boolean;
    panelClass: string;
    displayedColumns: string[] = ['appId', 'appName', 'metricUrl', 'editmetricUrl'];
    dataSource: StatsDataSource;
    private instanceChange: Subscription;
    private instanceKey: string;
    metricsUrl: SafeResourceUrl;
    tabs = [];
    selected = new FormControl(0);

    constructor(private statsservice: StatsService,
        private httpClient: HttpClient,
        private sanitize: DomSanitizer, 
        private errorDialogService: ErrorDialogService,
        private confirmDialogService: ConfirmDialogService,
        private notificationService: NotificationService,
        private loadingDialogService: LoadingDialogService,
        public instanceSelectorService: InstanceSelectorService,
        public dialog: MatDialog,
        public ui: UiService) {
    }

    ngOnInit() {
        this.dataSource = new StatsDataSource(this.statsservice, this.notificationService);

        this.ui.darkModeState.subscribe((isDark) => {
            this.darkMode = isDark;
        });

        this.instanceChange = this.instanceSelectorService.getSelectedInstance().subscribe((instance: RicInstance) => {
            if (instance.key) {
                this.instanceKey = instance.key;
                this.dataSource.loadTable(instance.key);
              }
        });
    
    }

    ngOnDestroy() {
        this.instanceChange.unsubscribe();
    }

    setupAppMetrics() {
        if (this.darkMode) {
          this.panelClass = 'dark-theme';
        } else {
          this.panelClass = '';
        }
        const dialogRef = this.dialog.open(StatsDialogComponent, {
          panelClass: this.panelClass,
          width: '450px',
          data: {
            instanceKey: this.instanceKey
          }
        });
        dialogRef.afterClosed()
          .subscribe((result: boolean) => {
            if (result) {
              this.dataSource.loadTable(this.instanceKey);
            }
          });
    }

    editAppMetrics(stats?) {
        const dialogRef = this.dialog.open(StatsDialogComponent, {
          hasBackdrop: false,
          data: {
            instanceKey: this.instanceKey,
            appName: stats ? stats.appName : '',
            metricUrl: stats.metricUrl ? stats.metricUrl : '',
            appId: stats.appId ? stats.appId : 0,
            isEdit: 'true'
          }
        });
        dialogRef.afterClosed()
          .subscribe((result: boolean) => {
            if (result) {
              this.dataSource.loadTable(this.instanceKey);
            }
          });
      }
    
      viewAppMetrics(stats?) {
        this.statsservice.getAppMetricsById(this.instanceKey, stats.appId)  .subscribe((res: StatsDetails) => {
          this.metricsUrl = this.sanitize.bypassSecurityTrustResourceUrl(res.metricUrl);
          let tabNotThere:boolean = true;
          if (this.tabs.length <= 0) {
            this.tabs.push(res);
            this.selected.setValue(this.tabs.length - 1);
          }
          else {
            for(let i=0; i<this.tabs.length; i++){
              if (this.tabs[i].appId == res.appId) {
                this.tabs[i].appName = res.appName;
                this.tabs[i].metricUrl = res.metricUrl;
                this.selected.setValue(i);
                tabNotThere  = false;
                break;
              }
            }
            if (tabNotThere) {
              this.tabs.push(res);
              this.selected.setValue(this.tabs.length - 1);
            }
          }
        });  
      }
      
      onTabChanged(event: MatTabChangeEvent) 
      {
        this.viewAppMetrics(this.tabs[event.index]);
      }
}
