import { NgModule } from '@angular/core';

import { ChartJSComponent } from './chartjs.component';
import { ChartJSRoutingModule } from './chartjs-routing.module';
import {ChartsModule} from 'ng2-charts';

@NgModule({
  imports: [
    ChartJSRoutingModule,
    ChartsModule
  ],
  declarations: [ ChartJSComponent ]
})
export class ChartJSModule { }
