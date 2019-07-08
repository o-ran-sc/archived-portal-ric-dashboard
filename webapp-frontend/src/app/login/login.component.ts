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
import { Router } from '@angular/router';
import { DashboardService } from '../services/dashboard/dashboard.service';
import { DashboardSuccessTransport, LoginTransport } from '../interfaces/dashboard.types';

@Component({
  selector: 'rd-main',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})

export class LoginComponent implements OnInit {

  loginForm: FormGroup;

  constructor(private dashboardService: DashboardService,
    private router: Router) { }

  ngOnInit() {
    this.loginForm = new FormGroup({
      username: new FormControl('', [Validators.required]),
      password: new FormControl('', [Validators.required])
    });
    this.dashboardService.clearUserToken();
  }

  public validateControl(controlName: string) {
    if (this.loginForm.controls[controlName].invalid && this.loginForm.controls[controlName].touched) {
      return true;
    }
    return false;
  }

  public hasError(controlName: string, errorName: string) {
    if (this.loginForm.controls[controlName].hasError(errorName)) {
      return true;
    }
    return false;
  }

  login(loginFormValue: LoginTransport) {
    if (this.loginForm.valid) {
      this.dashboardService.login(loginFormValue.username, loginFormValue.password)
        .subscribe((r: DashboardSuccessTransport) => {
          this.dashboardService.setUserToken(loginFormValue.username);
          this.router.navigate(['']);
        },
          (err: any) => {
            alert('Login failed.');
          });
    }
  }

}
