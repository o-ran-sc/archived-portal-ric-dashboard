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
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { E2ManagerService } from '../services/e2-mgr/e2-mgr.service';
import { NotificationService } from '../services/ui/notification.service';
import { ErrorDialogService } from '../services/ui/error-dialog.service';
import { E2SetupRequest } from '../interfaces/e2-mgr.types';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
    selector: 'rd-ran-control-connect-dialog',
    templateUrl: './ran-connection-dialog.component.html',
    styleUrls: ['./ran-connection-dialog.component.css']
})

export class RanControlConnectDialogComponent implements OnInit {

    public ranDialogForm: FormGroup;

    constructor(
        private dialogRef: MatDialogRef<RanControlConnectDialogComponent>,
        private service: E2ManagerService,
        private errorService: ErrorDialogService,
        private notifService: NotificationService,
        @Inject(MAT_DIALOG_DATA) public data: E2SetupRequest) {
    }

    ngOnInit() {
        const namePattern = /^([A-Z]){4}([0-9]){6}$/;
        // tslint:disable-next-line:max-line-length
        const ipPattern = /((^\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\s*$)|(^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$))/;
        const portPattern = /^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$/;
        this.ranDialogForm = new FormGroup({
            ranType: new FormControl('endc'),
            ranName: new FormControl('', [Validators.required, Validators.pattern(namePattern)]),
            ranIp: new FormControl('', [Validators.required, Validators.pattern(ipPattern)]),
            ranPort: new FormControl('', [Validators.required, Validators.pattern(portPattern)])
        });

    }

    onCancel() {
        this.dialogRef.close();
    }

    public setupConnection = (ranFormValue) => {
        if (this.ranDialogForm.valid) {
            this.executeSetupConnection(ranFormValue);
        }
    }

    private executeSetupConnection = (ranFormValue) => {
        let httpErrRes: HttpErrorResponse;
        const aboutError = 'RAN Connection Failed: ';
        const setupRequest: E2SetupRequest = {
            ranName: ranFormValue.ranName,
            ranIp: ranFormValue.ranIp,
            ranPort: ranFormValue.ranPort
        };
        if (ranFormValue.ranType === 'endc') {
            this.service.endcSetup(setupRequest).subscribe(
                (response: any) => {
                    this.notifService.success('Endc connect succeeded!');
                    this.dialogRef.close();
                },
                (error => {
                    httpErrRes = error;
                    this.errorService.displayError(aboutError + httpErrRes.message);
                })
            );
        } else {
            this.service.x2Setup(setupRequest).subscribe(
                (response: any) => {
                    this.notifService.success('X2 connect succeeded!');
                    this.dialogRef.close();
                },
                (error => {
                    httpErrRes = error;
                    this.errorService.displayError(aboutError + httpErrRes.message);
                })
            );
        }
    }

    public hasError(controlName: string, errorName: string) {
        if (this.ranDialogForm.controls[controlName].hasError(errorName)) {
          return true;
        }
        return false;
    }

    public validateControl(controlName: string) {
        if (this.ranDialogForm.controls[controlName].invalid && this.ranDialogForm.controls[controlName].touched) {
            return true;
        }
        return false;
    }

} // class AppRANConnectDialog
