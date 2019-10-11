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
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})

export class StatsService {
    baseJSONServerUrl = 'http://localhost:3000';
    dataMetrics = [{}];
    latencyMetrics;
    load: Observable<number>;
    cpuMetrics;
    hostURL = 'http://localhost:10080';
    jsonURL = 'http://localhost:3000';
    metricsPath = '/a1ric/metrics';
    delayPath = '/a1ric/delay';
    loadPath = '/a1ric/load';
    delayMax = '15';
    loadMax = '100000';
    httpOptions = {
            headers: new HttpHeaders({
              'Content-Type':  'application/json',
              'Access-Control-Allow-Origin': '*',
              'Access-Control-Allow-Methods': '*',
              'Access-Control-Allow-Credentials': 'true',
              'Access-Control-Allow-Headers': 'Origin, X-Requested-With, Content-Type, Accept, Authorization'
            })
          };

    private basePath = 'api/admin/';

    constructor(private httpClient: HttpClient) {
        // this.loadConfig();
        // this.getLoad();
    }

    getMetrics() {

        return this.dataMetrics; // @TODO implement the service to fetch the backend data
    }

    getLatencyMetrics() {
        this.latencyMetrics = this.getRandomValue();
        return this.latencyMetrics;
    }

    getLoad(): Observable<number> {
        // this.loadMetrics = this.getRandomValue();
        this.httpClient.get(this.hostURL + this.loadPath).subscribe((res) => {
            console.log(res);
            console.log('stats.service.getLoad(): ' + res['load']);
            this.load = res['load'];
            return this.load;
        });
        return this.load;
    }

    putLoad(value: number) {
        // this.loadMetrics = this.getRandomValue();
        const jsonValue = '{ "load": ' + value + ' }';
        console.log(jsonValue);
        this.httpClient.put(this.hostURL + this.loadPath, jsonValue , this.httpOptions).subscribe((res) => {
            console.log(res);
        });
    }

    putDelay(value: number) {
        // this.loadMetrics = this.getRandomValue();
        const jsonValue = '{ "delay": ' + value + ' }';
        console.log(jsonValue);
        this.httpClient.put(this.hostURL + this.delayPath, jsonValue , this.httpOptions).subscribe((res) => {
            console.log(res);
        });
    }

    getCpuMetrics() {
        this.cpuMetrics = this.getRandomValue();
        return this.cpuMetrics;
    }

    getRandomValue() {
        return Math.round((Math.random() * (20 - 0)) + 0);
    }

    // Gets xApp metrics kibana url for the named application
    getAppMetricsUrl(appName: string)  {
        return this.httpClient.get(this.basePath + 'metrics', {
            params: new HttpParams()
                .set('app', appName)
        });
    }

    saveConfig(key: string, value: string) {
        if (key === 'jsonURL') {
            this.baseJSONServerUrl = value;
        }
        console.log('save this.baseJSONServerUrl ' + this.baseJSONServerUrl);
        const jsonValue = '{"id": "' + key + '", "value": "' + value + '"}';
        console.log(jsonValue);
        this.httpClient.put(this.baseJSONServerUrl + '/config/' + key , jsonValue, this.httpOptions).subscribe((res) => {
            console.log(res);
        });


    }

    loadConfig() {
        console.log('load this.baseJSONServerUrl ' + this.baseJSONServerUrl);
        const httpOptions = {
                headers: new HttpHeaders({
                  'Content-Type':  'application/json'
                })
              };
        this.httpClient.get(this.baseJSONServerUrl + '/config/', httpOptions).subscribe((res) => {
            console.log(res);
            this.jsonURL = res[0].value;
            this.hostURL = res[1].value;
            this.metricsPath = res[2].value;
            this.delayPath = res[3].value;
            this.loadPath = res[4].value;
            this.delayMax = res[5].value;
            this.loadMax = res[6].value;
        },
        (her: HttpErrorResponse) => {
            console.log ('loadConfig failed: ' + her.message);
          });
    }
}

interface Delay {
    delay: number;
}
