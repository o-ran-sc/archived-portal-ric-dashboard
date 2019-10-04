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

import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { LoadingDialogComponent } from './../../ui/loading-dialog/loading-dialog.component';

@Injectable({
  providedIn: 'root'
})
export class LoadingDialogService {

  constructor(private dialog: MatDialog) { }

  private loadingDialogRef: MatDialogRef<LoadingDialogComponent>;

  startLoading(msg: string) {
    this.loadingDialogRef = this.dialog.open(LoadingDialogComponent, {
      disableClose: true,
      width: '480px',
      position: { top: '100px' },
      data: {
        message: msg
      }
    });
  }

  stopLoading() {
    this.loadingDialogRef.close()
  }

}

