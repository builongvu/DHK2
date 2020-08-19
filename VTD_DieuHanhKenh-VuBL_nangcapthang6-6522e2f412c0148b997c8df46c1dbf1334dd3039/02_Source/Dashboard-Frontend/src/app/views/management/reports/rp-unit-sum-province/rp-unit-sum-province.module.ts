import { WarningConfigModule } from './../../warning-config/warning-config.module';
import {CUSTOM_ELEMENTS_SCHEMA, NgModule, NO_ERRORS_SCHEMA} from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatSelectModule } from '@angular/material/select';
import {MatInputModule} from '@angular/material/input';
import {RpUnitSumProvinceRoutingModule} from './rp-unit-sum-province.routing.module';
import {RpUnitSumProvinceComponent} from './rp-unit-sum-province.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ButtonsModule, ModalModule, TooltipConfig, TooltipModule} from 'ngx-bootstrap';
import {NgxPaginationModule} from 'ngx-pagination';
import {MatTableModule} from '@angular/material/table';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {ToastrModule} from 'ngx-toastr';
import {DownloadService} from '../../../../services/management/download.service';
import {TreeviewModule} from 'ngx-treeview';
import {TranslateModule} from '@ngx-translate/core';
import {ConfigTreeviewModule} from '../../../../config-treeview/config-treeview.module';
import { SqlQueryService } from '../../../../services/management/sql.service';
import { UnitService } from '../../../../services/management/unit.service';

@NgModule({
  declarations: [
    RpUnitSumProvinceComponent
  ],
  imports: [
    MatFormFieldModule, MatInputModule,
    MatSelectModule,
    FormsModule,
    CommonModule,
    ModalModule.forRoot(),
    ButtonsModule.forRoot(),
    ReactiveFormsModule,
    NgxPaginationModule,
    MatTableModule,
    MatPaginatorModule,
    MatDatepickerModule,
    ToastrModule.forRoot(),
    RpUnitSumProvinceRoutingModule,
    TooltipModule,
    TreeviewModule.forRoot(),
    TranslateModule,
    ConfigTreeviewModule,
    WarningConfigModule
  ],
  providers: [
    DownloadService,
    TooltipConfig,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
})
export class RpUnitSumProvinceModule { }
