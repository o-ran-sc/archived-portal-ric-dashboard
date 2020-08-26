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
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { Subscription } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { RicInstance } from '../interfaces/dashboard.types';
import { XMXapp } from '../interfaces/app-mgr.types';
import { AppMgrService } from '../services/app-mgr/app-mgr.service';
import { InstanceSelectorService } from '../services/instance-selector/instance-selector.service';
import { LoadingDialogService } from '../services/ui/loading-dialog.service';
import { UiService } from '../services/ui/ui.service';
import { AppConfigurationComponent } from './../app-configuration/app-configuration.component';
import { ConfirmDialogService } from '../services/ui/confirm-dialog.service';
import { DeployDialogComponent } from '../ui/deploy-dialog/deploy-dialog.component';
import { OnboardComponent } from './../onboard/onboard.component';
import { NotificationService } from '../services/ui/notification.service';
import { CatalogDataSource } from './catalog.datasource';

@Component({
  selector: 'rd-app-catalog',
  templateUrl: './catalog.component.html',
  styleUrls: ['./catalog.component.scss'],
})
export class CatalogComponent implements OnInit, OnDestroy {

  darkMode: boolean;
  panelClass: string;
  displayedColumns: string[] = ['name', 'version', 'action'];
  dataSource: CatalogDataSource;
  private instanceChange: Subscription;
  private instanceKey: string;

  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    private appMgrService: AppMgrService,
    private confirmDialogService: ConfirmDialogService,
    private dialog: MatDialog,
    private loadingDialogService: LoadingDialogService,
    private notificationService: NotificationService,
    public instanceSelectorService: InstanceSelectorService,
    public ui: UiService) { }

  ngOnInit() {
    this.dataSource = new CatalogDataSource(this.appMgrService, this.sort, this.notificationService);
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

  onConfigureApp(xapp: XMXapp): void {
    if (this.darkMode) {
      this.panelClass = 'dark-theme';
    } else {
      this.panelClass = '';
    }
    const dialogRef = this.dialog.open(AppConfigurationComponent, {
      panelClass: this.panelClass,
      width: '40%',
      maxHeight: '500px',
      position: {
        top: '10%'
      },
      data: {
        xapp: xapp,
        instanceKey: this.instanceKey
      }

    });
  }

  onDeployApp(app: XMXapp): void {
    if (this.darkMode) {
      this.panelClass = 'dark-theme';
    } else {
      this.panelClass = '';
    }
    const dialogRef = this.dialog.open(DeployDialogComponent, {
      panelClass: this.panelClass,
      width: '400px',
      maxHeight: '1000px',
      position: {
        top: '10%'
      },
      data: {
        xappName: app.name,
        instanceKey: this.instanceKey
      }

    });
  }
/*    this.confirmDialogService.openConfirmDialog('Deploy application ' + app.name + '?')
      .afterClosed().subscribe((res: boolean) => {
        if (res) {
          this.loadingDialogService.startLoading('Deploying ' + app.name);
          this.appMgrService.deployXapp(this.instanceKey, app.name)
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
  }*/

  onboard(): void {
    if (this.darkMode) {
      this.panelClass = 'dark-theme';
    } else {
      this.panelClass = '';
    }
    const dialogRef = this.dialog.open(OnboardComponent, {
      panelClass: this.panelClass,
      width: '400px',
      maxHeight: '1000px',
      position: {
        top: '10%'
      },
      data: {
        instanceKey: this.instanceKey
      }

    });
  }

}
