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
import { finalize } from 'rxjs/operators';
import { RicInstance } from './interfaces/dashboard.types';
import { InstanceSelectorService } from './services/instance-selector/instance-selector.service';
import { LoadingDialogService } from './services/ui/loading-dialog.service';
import { UiService } from './services/ui/ui.service';

@Component({
  selector: 'rd-root',
  templateUrl: './rd.component.html',
  styleUrls: ['./rd.component.scss']
})
export class RdComponent implements OnInit {
  showMenu = false;
  darkModeActive: boolean;
  private instanceArray: RicInstance[];
  private selectedInstanceKey: string ;

  constructor(
    public ui: UiService,
    private instanceSelectorService: InstanceSelectorService,
    private loadingDialogService: LoadingDialogService,) {
  }

  ngOnInit() {
    this.ui.darkModeState.subscribe((value) => {
      this.darkModeActive = value;
    });
    this.loadingDialogService.startLoading('loading RIC instances');
    this.instanceSelectorService.getInstanceArray()
      .pipe(
      finalize(() => this.loadingDialogService.stopLoading())
    ).subscribe((instanceArray: RicInstance[]) => {
      this.instanceArray = instanceArray;
      this.selectedInstanceKey = this.instanceSelectorService.getSelectedInstancekey()
    })

  }

  toggleMenu() {
    this.showMenu = !this.showMenu;
  }

  modeToggleSwitch() {
    this.ui.darkModeState.next(!this.darkModeActive);
  }

  changeInstance(selectedInstancekey: string) {
    this.instanceSelectorService.updateSelectedInstance(selectedInstancekey);
  }

}
