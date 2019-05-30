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

import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { ACAdmissionIntervalControl, ACAdmissionIntervalControlAck } from '../interfaces/ac-xapp.types';
import { AcXappService } from '../services/ac-xapp/ac-xapp.service';
import { ErrorDialogService } from '../services/ui/error-dialog.service';
import { NotificationService } from './../services/ui/notification.service';

@Component({
  selector: 'app-ac-xapp',
  templateUrl: './ac-xapp.component.html',
  styleUrls: ['./ac-xapp.component.scss']
})
export class AcXappComponent implements OnInit {

  private acForm: FormGroup;

  // this is probably the A1 version string
  acVersion: string;

  constructor(
    private acXappService: AcXappService,
    private errorDialogService: ErrorDialogService,
    private notificationService: NotificationService) { }

  ngOnInit() {
    const windowLengthPattern = /^([0-9]{1}|[1-5][0-9]{1}|60)$/;
    const blockingRatePattern = /^([0-9]{1,2}|100)$/;
    const triggerPattern = /^([0-9]+)$/;
    // No way to fetch current settings via A1 at present,
    // so populate form fields with reasonable defaults.
    this.acForm = new FormGroup({
      enforce: new FormControl(true,  [Validators.required]),
      windowLength: new FormControl('', [Validators.required, Validators.pattern(windowLengthPattern)]),
      blockingRate: new FormControl('', [Validators.required, Validators.pattern(blockingRatePattern)]),
      triggerThreshold: new FormControl('', [Validators.required, Validators.pattern(triggerPattern)])
    });
    this.acXappService.getVersion().subscribe((res: string) => this.acVersion = res);
  }

  updateAc = (acFormValue: ACAdmissionIntervalControl) => {
    if (this.acForm.valid) {
      this.acXappService.putPolicy(acFormValue).subscribe(
        response => {
          if (response.status === 200 ) {
            this.notificationService.success('AC update policy succeeded!');
          }
        },
        (error => {
          this.errorDialogService.displayError('AC update policy failed: ' + error.message);
        })
      );
    }
  }

  hasError(controlName: string, errorName: string) {
    if (this.acForm.controls[controlName].hasError(errorName)) {
      return true;
    }
    return false;
  }

  validateControl(controlName: string) {
    if (this.acForm.controls[controlName].invalid && this.acForm.controls[controlName].touched) {
      return true;
    }
    return false;
  }

}
