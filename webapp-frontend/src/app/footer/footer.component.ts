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
import { Component, OnInit } from '@angular/core'
import { Observable } from 'rxjs/Rx'
import { VersionService } from '../services/version/version.service'
import { SuccessTransport } from '../interfaces/dashboard.types'

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent implements OnInit {

  public dashboardVersion : string
 
  // Inject the service
  constructor(public versionService: VersionService) { }

  ngOnInit() {
    var dashboardVersionObs = this.versionService.getDashboardVersion()
    dashboardVersionObs.subscribe((res : SuccessTransport) => this.dashboardVersion = res.data)
  }

}
