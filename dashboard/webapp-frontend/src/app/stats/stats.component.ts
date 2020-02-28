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
import { DashboardSuccessTransport } from '../interfaces/dashboard.types';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
    selector: 'rd-stats',
    templateUrl: './stats.component.html',
    styleUrls: ['./stats.component.scss']
})
export class StatsComponent implements OnInit {

    @ViewChildren(BaseChartDirective) charts: QueryList<BaseChartDirective>;
    checked = false;
    metricsUrlDc: SafeResourceUrl;
    metricsUrlMc: SafeResourceUrl;

    constructor(private service: StatsService,
        private httpClient: HttpClient,
        private sanitize: DomSanitizer) {
    }

    ngOnInit() {
        this.service.getAppMetricsUrl('DC').subscribe((res: DashboardSuccessTransport) => {
            this.metricsUrlDc = this.sanitize.bypassSecurityTrustResourceUrl(res.data);
        });
        this.service.getAppMetricsUrl('MC').subscribe((res: DashboardSuccessTransport) => {
            this.metricsUrlMc = this.sanitize.bypassSecurityTrustResourceUrl(res.data);
        });
    }

}
