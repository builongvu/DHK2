import {ConfigTreeviewModule} from './../../config-treeview/config-treeview.module';
import {NgModule, NO_ERRORS_SCHEMA} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {BsDropdownModule} from 'ngx-bootstrap/dropdown';
import {ButtonsModule} from 'ngx-bootstrap/buttons';
import {AngularResizedEventModule} from 'angular-resize-event';
import {DashboardComponent} from './dashboard.component';
import {DashboardRoutingModule} from './dashboard-routing.module';
import {CommonModule} from '@angular/common';
import {TooltipModule} from 'ngx-bootstrap';
import {LinechartComponent} from './card/contents/linechart/linechart.component';
import {TableComponent} from './card/contents/table/table.component';
import {CardComponent} from './card/card.component';
import {BaseComponent} from './card/basechart/base.component';
import {ToggerComponent} from './togger/togger.component';
import {
    MatDatepickerModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule, MatRadioModule,
    MatSelectModule,
    MatTabsModule,
    MatTooltipModule
} from '@angular/material';
import {TranslateModule} from '@ngx-translate/core';
import {TreeviewModule} from 'ngx-treeview';
import {ConfigTreeComponent} from './togger/config-tree/config-tree.component';
import {ContentsComponent} from './card/contents/contents.component';
import {ContentTopComponent} from './card/contents/rank-staff/content-top.component';
import {SumaryComponent} from './card/contents/summary/sumary.component';
import {ClickOutsideModule} from 'ng-click-outside';
import {BarChartComponent} from './card/contents/bar-chart';
import {ColumnChartComponent} from './card/contents/column-chart/column-chart.component';
import {FilterPipe} from '../../services/management/filter.pipe';
import {NgMultiSelectDropDownModule} from 'ng-multiselect-dropdown';
import { TargetEditComponent } from '../management/mng-target/target-edit/target-edit.component';
import { DialogObjectComponent } from '../management/objectconfig/dialog-object/dialog-object.component';
import {ChartsModule} from 'ng2-charts';
import { ShopUnitComponent } from '../management/shop-unit/shop-unit.component';
import { NgxPaginationModule } from 'ngx-pagination';
import { DialogShopUnitComponent } from '../management/shop-unit/dialog-shop-unit/dialog-shop-unit.component';
import { UnitComponent } from '../management/shop-unit/unit/unit.component';
import { DialogUnitComponent } from '../management/shop-unit/dialog-unit/dialog-unit.component';
import { ConsecutiveWarningComponent } from './card/contents/consecutive-warning/consecutive-warning.component';
import { Spark3Component } from './card/contents/spark3/spark3.component';
import { Spark4Component } from './card/contents/spark4/spark4.component';

@NgModule({
    imports: [
        FormsModule,
        DashboardRoutingModule,
        ChartsModule,
        AngularResizedEventModule,
        BsDropdownModule,
        ButtonsModule.forRoot(),
        CommonModule,
        TooltipModule,
        MatFormFieldModule,
        MatTabsModule,
        TranslateModule,
        MatDatepickerModule,
        MatInputModule,
        ReactiveFormsModule,
        TreeviewModule.forRoot(),
        MatProgressSpinnerModule,
        ClickOutsideModule,
        MatSelectModule,
        NgxPaginationModule,
        ConfigTreeviewModule,
        NgMultiSelectDropDownModule,
        MatDialogModule,
        MatTooltipModule,
        MatRadioModule,
        // AppModule,
    ],
  declarations: [
    DashboardComponent,
    LinechartComponent,
    TableComponent,
    CardComponent,
    BaseComponent,
    ToggerComponent,
    ConfigTreeComponent,
    ContentsComponent,
    ContentTopComponent,
    SumaryComponent,
    BarChartComponent,
    ColumnChartComponent,
    FilterPipe,
    TargetEditComponent,
    DialogObjectComponent,
    ShopUnitComponent,
    DialogShopUnitComponent,
    UnitComponent,
    DialogUnitComponent,
    ConsecutiveWarningComponent,
    Spark3Component,
    Spark4Component
  ],
  providers: [
    FilterPipe
  ],
  schemas: [NO_ERRORS_SCHEMA],
  exports: [
    ConfigTreeComponent
  ],
  entryComponents: [TargetEditComponent, DialogObjectComponent, DialogShopUnitComponent, DialogUnitComponent]
})
export class DashboardModule {
}
