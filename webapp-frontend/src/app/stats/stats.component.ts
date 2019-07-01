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
import { Component, OnInit, ViewChildren, QueryList } from '@angular/core';
import { BaseChartDirective } from 'ng2-charts/ng2-charts';
import { StatsService } from '../services/stats/stats.service';
import { MatSlideToggleChange } from "@angular/material/slide-toggle";
import { HttpClient } from '@angular/common/http';
import { HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";
import { HttpErrorResponse } from "@angular/common/http";
import { map } from 'rxjs/operators';

@Component({
    selector: 'rd-stats',
    templateUrl: './stats.component.html',
    styleUrls: ['./stats.component.scss']
})
export class StatsComponent implements OnInit {

    @ViewChildren(BaseChartDirective) charts: QueryList<BaseChartDirective>;
    timeLeft = 60;
    interval;
    checked = false;
    load;
    delay;

    public latencyChartColors: Array<any> = [
        { // blue
            backgroundColor: 'rgba(197, 239, 247, 0.2)',
            borderColor: 'lightblue',
            pointBackgroundColor: 'lightblue',
            pointBorderColor: '#fff',
            pointHoverBackgroundColor: '#fff',
            pointHoverBorderColor: 'rgba(148,159,177,0.8)'
        }
    ];
    public latencyChartOptions = {
        scaleShowVerticalLines: true,
        responsive: true,
        animation: {
            duration: 800 * 1.5,
            easing: 'linear'
        },
        hover: {
            animationDuration: 1 // duration of animations when hovering an item
        },
        responsiveAnimationDuration: 500,
        scales: {
            yAxes: [{
                ticks: {
                    // the data minimum used for determining the ticks is Math.min(dataMin, suggestedMin)
                    suggestedMin: 0,
                    // the data maximum used for determining the ticks is Math.max(dataMax, suggestedMax)
//                    suggestedMax: 1000
                },
                scaleLabel: {
                    display: true,
                    labelString: 'msecs'
                }
            }],
            xAxes: [{
                scaleLabel: {
                    display: true,
                    labelString: 'time (last 10 seconds)'
                }
            }]
        },
    };
    public latencyChartLabels = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10'];
    public latencyChartType = 'line';
    public latencyChartLegend = true;
    public latencyChartData = [
        { data: [65, 59, 80, 81, 56, 55, 40, 20, 12, 34], label: 'Latency' },
    ];

    public loadChartColors: Array<any> = [

        { // green
            backgroundColor: 'rgba(200, 247, 197, 0.2)',
            borderColor: 'lightgreen',
            pointBackgroundColor: 'lightgreen',
            pointBorderColor: '#fff',
            pointHoverBackgroundColor: '#fff',
            pointHoverBorderColor: 'rgba(0,200,0,0.5)'
        }
    ];
    public loadChartOptions = {
        scaleShowVerticalLines: false,
        responsive: true,
        animation: {
            duration: 800 * 1.5,
            easing: 'linear'
        },
        hover: {
            animationDuration: 1 // duration of animations when hovering an item
        },
        responsiveAnimationDuration: 500,
        scales: {
            yAxes: [{
                ticks: {
                    // the data minimum used for determining the ticks is Math.min(dataMin, suggestedMin)
                    suggestedMin: 0,
                    // the data maximum used for determining the ticks is Math.max(dataMax, suggestedMax)
//                    suggestedMax: 1000
                },
                scaleLabel: {
                    display: true,
                    labelString: '# of requests'
                }
            }],
            xAxes: [{
                scaleLabel: {
                    display: true,
                    labelString: 'time (last 10 seconds)'
                }
            }]
        },
    };
    public loadChartLabels = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10'];
    public loadChartType = 'line';
    public loadChartLegend = true;
    public loadChartData = [
        { data: [28, 48, 40, 19, 86, 77, 90, 20, 12, 34], label: 'Load' }
    ];

    public cpuChartColors: Array<any> = [

        { // red
            backgroundColor: 'rgba(241, 169, 160, 0.2)',
            borderColor: 'brown',
            pointBackgroundColor: 'brown',
            pointBorderColor: '#fff',
            pointHoverBackgroundColor: '#fff',
            pointHoverBorderColor: 'rgba(0,200,0,0.5)'
        }
    ];
    public cpuChartOptions = {
        scaleShowVerticalLines: false,
        responsive: true,
        animation: {
            duration: 800 * 1.5,
            easing: 'linear'
        },
        hover: {
            animationDuration: 1 // duration of animations when hovering an item
        },
        responsiveAnimationDuration: 500,
        scales: {
            yAxes: [{
                ticks: {
                    // the data minimum used for determining the ticks is Math.min(dataMin, suggestedMin)
                    suggestedMin: 0,
                    // the data maximum used for determining the ticks is Math.max(dataMax, suggestedMax)
//                    suggestedMax: 1000
                },
                scaleLabel: {
                    display: true,
                    labelString: '# of requests'
                }
            }],
            xAxes: [{
                scaleLabel: {
                    display: true,
                    labelString: 'time (last 10 seconds)'
                }
            }]
        },
    };
    public cpuChartLabels = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10'];
    public cpuChartType = 'line';
    public cpuChartLegend = true;
    public cpuChartData = [
        { data: [15, 29, 30, 31, 53, 52, 41, 70, 32, 14], label: 'RICLoad' }
    ];

    public x = 11;

    public y = 11;

    public z = 11;
    public loop = true;

    latencyClickData() {
        // this.latencyChartData = [{data: [Math.random() * 100, Math.random() * 100, Math.random() * 100,
        // Math.random() * 100, Math.random() * 100, Math.random() * 100, Math.random() * 100, Math.random() * 100,
        // Math.random() * 100, Math.random() * 100], label: 'Latency'}];
        this.charts.forEach((child) => {
            if (child.datasets[0].label === 'Latency') {
                this.latencyChartLabels.shift();
                child.datasets[0].data.shift();

                const latencyData = this.service.getLatencyMetrics();
                child.datasets[0].data.push(latencyData);
                this.latencyChartLabels.push('' + this.x++);
            }
            // once new data is computed and datasets are updated, tell our baseChart the datasets changed
            child.ngOnChanges({
                datasets: {
                    currentValue: child.datasets,
                    previousValue: null,
                    firstChange: true,
                    isFirstChange: () => true
                }
            });
        });
    }

    loadClickData() {
        if (this.loop) {
            this.loop = false;
            this.startLoadTimer();
        } else {
            this.loop = true;
            this.pauseLoadTimer();
        }
    }

    loopLoadData(metricsv: any) {
        this.charts.forEach((child) => {
            if (child.datasets[0].label === 'Load') {
                this.loadChartLabels.shift();
                child.datasets[0].data.shift();

                //const loadData = this.service.getLoad();
                //child.datasets[0].data.push(this.service.load);
                child.datasets[0].data.push(metricsv['load']);
                this.loadChartLabels.push('' + this.x++);
            }
            if (child.datasets[0].label === 'Latency') {
                this.latencyChartLabels.shift();
                child.datasets[0].data.shift();

                //const loadData = this.service.getLoad();
                //child.datasets[0].data.push(this.service.load);
                child.datasets[0].data.push(metricsv['latency']);
                this.latencyChartLabels.push('' + this.x++);
            }
            if (child.datasets[0].label === 'RICLoad') {
                this.latencyChartLabels.shift();
                child.datasets[0].data.shift();

                //const loadData = this.service.getLoad();
                //child.datasets[0].data.push(this.service.load);
                child.datasets[0].data.push(metricsv['ricload']);
                this.latencyChartLabels.push('' + this.x++);
            }
            // once new data is computed and datasets are updated, tell our baseChart the datasets changed
            child.ngOnChanges({
                datasets: {
                    currentValue: child.datasets,
                    previousValue: null,
                    firstChange: true,
                    isFirstChange: () => true
                }
            });
        });
    }

    cpuClickData() {
        // this.cpuChartData = [{data: [Math.random() * 100, Math.random() * 100, Math.random() * 100,
        // Math.random() * 100, Math.random() * 100, Math.random() * 100, Math.random() * 100, Math.random() * 100,
        // Math.random() * 100, Math.random() * 100], label: 'CPU'}];
        const cpuData = this.service.getLatencyMetrics();
        this.newDataPoint([cpuData], this.z++);
    }

    newDataPoint(dataArr = [100], label) {

        this.cpuChartData.forEach((dataset, index) => {
            this.cpuChartData[index] = Object.assign({}, this.cpuChartData[index], {
                data: [...this.cpuChartData[index].data, dataArr[index]]
            });
        });

        this.cpuChartLabels = [...this.cpuChartLabels, label];
        console.log(this.cpuChartLabels);
        console.log(this.cpuChartData);
    }
    
    public sliderLoadMax = Number(this.service.loadMax) || 0;
    
    public sliderDelayMax = Number(this.service.delayMax) || 0;

    formatLabel(value: number | null) {
        if (!value) {
            return 0;
        }

        if (value >= 1000) {
            return Math.round(value / 1000);
        }

        return value;
    }

    constructor(private service: StatsService, private httpClient: HttpClient) {
        this.sliderLoadMax = Number(this.service.loadMax) || 0;
        
        this.sliderDelayMax = Number(this.service.delayMax) || 0;
        console.log('this.sliderLoadMax: ' + this.sliderLoadMax);
        console.log('this.sliderDelayMax: ' + this.sliderDelayMax);
    }
    ngOnInit() {
        this.fetchLoad().subscribe(loadv => {
          console.log('loadv: ' + loadv);
          this.checked = loadv;
      });
        this.fetchDelay().subscribe(delayv => {
            console.log('delayv: ' + delayv);
            this.delay = delayv;
        });
        this.fetchMetrics().subscribe(metricsv => {
            console.log('metricsv.load: ' + metricsv['load']);
            
        });
    }

    startLoadTimer() {
        this.interval = setInterval(() => {
            if (this.timeLeft > 0) {
                this.timeLeft--;
                this.fetchMetrics().subscribe(metricsv => {
                    console.log('metricsv.load: ' + metricsv['latency']);
                    console.log('metricsv.load: ' + metricsv['load']);
                    console.log('metricsv.load: ' + metricsv['ricload']);
                    this.loopLoadData(metricsv);
                });
                
            } else {
                this.timeLeft = 60;
            }
        }, 1000);
    }

    pauseLoadTimer() {
        clearInterval(this.interval);
    }
    
    fetchMetrics() {
        return this.httpClient.get<any[]>(this.service.hostURL + this.service.metricsPath, this.service.httpOptions).pipe(map(res => {
            console.log(res);
            console.log(res['load']);
            return res;
        }));
    }

    fetchDelay() {
        return this.httpClient.get<any[]>(this.service.hostURL + this.service.delayPath, this.service.httpOptions).pipe(map(res => {
            console.log(res);
            console.log(res['delay']);
            const delayv = res['delay'];
            console.log(delayv);
            this.delay = delayv;
            return this.delay;
        }));
    }
    
    saveDelay() {
        console.log(this.delay);
        this.service.putDelay(this.delay);
    }
    
    fetchLoad() {
        return this.httpClient.get<any[]>(this.service.hostURL + this.service.loadPath, this.service.httpOptions).pipe(map(res => {
            console.log(res);
            console.log(res['load']);
            const loadv = res['load'];
            console.log(loadv);
            this.load = loadv;
            return this.load;
        }));
        
    }
    
    saveLoad() {
        console.log(this.load);
        this.service.putLoad(this.load);
    }

}
