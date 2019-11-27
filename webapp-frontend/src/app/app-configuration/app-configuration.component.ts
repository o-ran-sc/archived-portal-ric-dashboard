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

import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { finalize } from 'rxjs/operators';
import { AppMgrService } from '../services/app-mgr/app-mgr.service';
import { ErrorDialogService } from '../services/ui/error-dialog.service';
import { LoadingDialogService } from '../services/ui/loading-dialog.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { NotificationService } from './../services/ui/notification.service';

@Component({
  selector: 'rd-app-configuration',
  templateUrl: './app-configuration.component.html',
  styleUrls: ['./app-configuration.component.scss']
})
export class AppConfigurationComponent implements OnInit {

  private loadingSubject = new BehaviorSubject<boolean>(false);
  public loading$ = this.loadingSubject.asObservable();

  constructor(
    private dialogRef: MatDialogRef<AppConfigurationComponent>,
    private appMgrService: AppMgrService,
    private errorDiaglogService: ErrorDialogService,
    private loadingDialogService: LoadingDialogService,
    private notificationService: NotificationService,
    @Inject(MAT_DIALOG_DATA) private data
  ) { }

  xappMetadata: any;
  xappConfigSchema: any;
  xappConfigData: any;
  xappLayout: any;
  ngOnInit() {
    this.loadingSubject.next(true);
    this.appMgrService.getConfig()
      .pipe(
        finalize(() => this.loadingSubject.next(false))
      )
      .subscribe(
        (allConfig: any) => {
          this.loadConfigForm(this.data.xapp.name, allConfig)
        }
      );
  }

  updateconfig(event) {
    var config = {
      metadata: this.xappMetadata,
      descriptor: this.xappConfigSchema,
      config: event,
      layout: this.xappLayout
    }
    this.loadingDialogService.startLoading("Updating " + this.data.xapp.name + " configuration");
    this.appMgrService.putConfig(this.data.instanceKey, config)
      .pipe(
        finalize(() => {
          this.loadingDialogService.stopLoading();
          this.dialogRef.close();
        })
      )
      .subscribe(
        (response: HttpResponse<Object>) => {
          this.notificationService.success('Configuration update succeeded!');
        },
        ((her: HttpErrorResponse) => {
          let msg = her.message;
          if (her.error && her.error.message) {
            msg = her.error.message;
          }
          this.notificationService.warn('Configuration update failed: ' + msg);
        })
      );
  }

  loadConfigForm(name: string, allConfig: any) {
    var xappConfig = allConfig.find(xapp => xapp.metadata.name == name)
    if (xappConfig != null) {
      this.xappMetadata = xappConfig.metadata
      this.xappConfigSchema = xappConfig.descriptor;
      this.xappConfigData = xappConfig.config;
      this.xappLayout = xappConfig.layout;
    } else {
      this.errorDiaglogService.displayError("Cannot find configration data for " + name);
      this.dialogRef.close();
    }
  }
}
