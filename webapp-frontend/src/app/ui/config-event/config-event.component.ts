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
import { Component, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { StatsService } from '../../services/stats/stats.service';

@Component({
  selector: 'rd-app-config-event',
  templateUrl: './config-event.component.html',
  styleUrls: ['./config-event.component.scss']
})
export class ConfigEventComponent implements OnInit {

    public renderValue;

    contactFormModalJsonUrl = new FormControl('', Validators.required);
    contactFormModalHost = new FormControl('', Validators.required);
    contactFormModalMetrics = new FormControl('', Validators.required);
    contactFormModalDelay = new FormControl('', Validators.required);
    contactFormModalLoad = new FormControl('', Validators.required);
    contactFormModalDelayMax = new FormControl('', Validators.required);
    contactFormModalLoadMax = new FormControl('', Validators.required);
    onOpened(event: any) {
        this.service.loadConfig();
        this.contactFormModalJsonUrl.setValue(this.service.jsonURL);
        this.contactFormModalHost.setValue(this.service.hostURL);
        this.contactFormModalMetrics.setValue(this.service.metricsPath);
        this.contactFormModalDelay.setValue(this.service.delayPath);
        this.contactFormModalLoad.setValue(this.service.loadPath);
        this.contactFormModalDelayMax.setValue(this.service.delayMax);
        this.contactFormModalLoadMax.setValue(this.service.loadMax);
        console.log(event);
    }


    constructor(private service: StatsService) {  }

    ngOnInit() {
        // load from file
       this.service.loadConfig();
       // console.log(this.service.hostURL);
       //this.contactFormModalHost.setValue(this.service.hostURL);
       //this.contactFormModalJsonUrl.setValue(this.service.jsonURL);
    }

    save() {
        // save to file
        this.service.saveConfig('jsonURL', this.contactFormModalJsonUrl.value);
        this.service.saveConfig('host', this.contactFormModalHost.value);
        this.service.saveConfig('metricspath', this.contactFormModalMetrics.value);
        this.service.saveConfig('delaypath', this.contactFormModalDelay.value);
        this.service.saveConfig('loadpath', this.contactFormModalLoad.value);
        this.service.saveConfig('delaymax', this.contactFormModalDelayMax.value);
        this.service.saveConfig('loadmax', this.contactFormModalLoadMax.value);
        this.service.loadConfig();
    }


}
