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
import { Component, OnInit, OnDestroy } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { RicInstance } from './interfaces/dashboard.types';
import { InstanceSelectorService } from './services/instance-selector/instance-selector.service';
import { InstanceSelectorDialogService } from './services/ui/instance-selector-dialog.service';
import { UiService } from './services/ui/ui.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'rd-root',
  templateUrl: './rd.component.html',
  styleUrls: ['./rd.component.scss']
})
export class RdComponent implements OnInit {
  showMenu = false;
  darkModeActive: boolean;

  private selectedInstanceName: string;
  private instanceChange: Subscription;
  private instanceKey: string;

  constructor(
    public ui: UiService,
    private istanceSelectorDialogService: InstanceSelectorDialogService,
    private instanceSelectorService: InstanceSelectorService) {
  }

  ngOnInit() {
    this.ui.darkModeState.subscribe((value) => {
      this.darkModeActive = value;
    });

    this.instanceChange = this.instanceSelectorService.getSelectedInstanceName().subscribe((instanceName: string) => {
      if (instanceName) {
        this.selectedInstanceName = instanceName;
      } else {
        this.openInstanceSelectorDialog()
      }
    });
  }

  ngOnDestroy() {
    this.instanceChange.unsubscribe();
  }

  modeToggleSwitch() {
    this.ui.darkModeState.next(!this.darkModeActive);
  }

  openInstanceSelectorDialog() {
    this.istanceSelectorDialogService.openInstanceSelectorDialog();
  }

}
