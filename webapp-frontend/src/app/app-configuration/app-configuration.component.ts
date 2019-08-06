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

import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AppMgrService } from '../services/app-mgr/app-mgr.service';
import { ErrorDialogService } from '../services/ui/error-dialog.service';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { finalize } from 'rxjs/operators';


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
    @Inject(MAT_DIALOG_DATA) private data
  ) { }

  xappMetadata: any;
  xappConfigSchema: any;
  xappConfigData: any; 

  ngOnInit() {
    this.loadingSubject.next(true);
    this.appMgrService.getConfig()
      .pipe(
        finalize(() => this.loadingSubject.next(false))
      )
      .subscribe(
      (allConfig: any) => {
        this.loadConfigForm(this.data.name, allConfig)
      }
    )
  }

  updateconfig(event) {
    var config = {
      metadata: this.xappMetadata,
      descriptor: this.xappConfigSchema,
      config: event
    }
    this.appMgrService.putConfig(config)
    this.dialogRef.close();
  }

  loadConfigForm(name: string, allConfig: any) {
    var xappConfig = allConfig.find(xapp => xapp.metadata.name == name)
    if (xappConfig != null) {
      this.xappMetadata = xappConfig.metadata
      this.xappConfigSchema = xappConfig.descriptor;
      this.xappConfigData = xappConfig.config;
    } else {
      this.errorDiaglogService.displayError("Cannot find configration data for " + name);
      this.dialogRef.close();
    }
  }
}
