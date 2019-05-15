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
import { ErrorHandlerService } from '../services/ui/error-handler.service';
import { E2SetupRequest } from '../interfaces/e2-mgr.types';

@Component({
    selector: 'app-signal-ranconnect-dialog',
    templateUrl: 'signal.component.ranconnect-dialog.html',
    styleUrls: ['signal.component.css']
})


export class AppRANConnectDialogComponent implements OnInit {


    public ranDialogForm: FormGroup;

    constructor(
        public dialogRef: MatDialogRef<AppRANConnectDialogComponent>,
        private service: E2ManagerService, private errorService: ErrorHandlerService,
        @Inject(MAT_DIALOG_DATA) public data: E2SetupRequest) {
    }

    private dialogConfig = {
            height: '300px',
            width: '400px',
            disableClose: true,
            data: {}
        };
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
        const setupRequest: E2SetupRequest = {
            ranName: ranFormValue.ranName,
            ranIp: ranFormValue.ranIp,
            ranPort: ranFormValue.ranPort
        };
        if (ranFormValue.ranType === 'endc') {
            this.service.endcSetup(setupRequest).subscribe((val: any[]) => {},
                (error => {
                    this.errorService.handleError(error, this.dialogConfig);
                })
            );
        } else {
            this.service.x2Setup(setupRequest).subscribe((val: any[]) => {},
                (error => {
                    this.errorService.handleError(error, this.dialogConfig);
                })
            );
        }
        this.dialogRef.close();
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
