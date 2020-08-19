import {CUSTOM_ELEMENTS_SCHEMA, NgModule, NO_ERRORS_SCHEMA} from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ButtonsModule } from 'ngx-bootstrap/buttons';
import { ModalModule } from 'ngx-bootstrap/modal';
import { NgxPaginationModule } from 'ngx-pagination';
import { VttTargetRoutingModule } from './vtt-target.routing.module';
import { VttTargetComponent } from './vtt-target.component';
import { ProductService } from '../../../services/management/product.service';
import { DownloadService } from '../../../services/management/download.service';
import { PlanmonthlyService } from '../../../services/management/planmonthly.service';
import { DatePipe } from '../../../services/management/date.pipe';
import { ChannelService } from '../../../services/management/channel.service';
import {
  MatCheckboxModule,
  MatDatepicker,
  MatDatepickerModule,
  MatFormFieldModule,
  MatInputModule,
  MatPaginatorModule,
  MatTableModule
} from '@angular/material';
import { MatSelectModule } from '@angular/material/select';
import {GroupsService} from '../../../services/management/group.service';
import {ServiceService} from '../../../services/management/service.service';
import {ToastrModule} from 'ngx-toastr';
import {TooltipConfig, TooltipModule} from 'ngx-bootstrap';
import {TreeviewModule} from 'ngx-treeview';
import {FilterPipe} from '../../../services/management/filter.pipe';
import {UnitStaffService} from '../../../services/management/unitStaff.service';
import {VttTargetLevelModule} from '../vtt-target-level/vtt-target-level.module';
import {TranslateModule} from '@ngx-translate/core';
import {ConfigTreeviewModule} from '../../../config-treeview/config-treeview.module';
import {DisabledOnSelectorDirective} from '../../../config-treeview/disabled-on-selector.directive';
import {DeleteService} from '../../../services/management/delete.service';
import {CycleService} from '../../../services/management/cycle.service';

@NgModule({
  imports: [
    MatFormFieldModule, MatInputModule,
    MatSelectModule,
    FormsModule,
    CommonModule,
    ModalModule.forRoot(),
    ButtonsModule.forRoot(),
    ReactiveFormsModule,
    NgxPaginationModule,
    VttTargetRoutingModule,
    MatTableModule,
    MatPaginatorModule,
    MatDatepickerModule,
    ToastrModule.forRoot(),
    TooltipModule,
    TreeviewModule.forRoot(), VttTargetLevelModule,
    TranslateModule,
    ConfigTreeviewModule, MatCheckboxModule
  ],
  providers: [
    ProductService,
    DownloadService,
    PlanmonthlyService,
    ChannelService,
    GroupsService,
    ServiceService,
    TooltipConfig,
    UnitStaffService,
    DeleteService,
    CycleService
  ],
  declarations: [
    VttTargetComponent,
    DatePipe
    // DisabledOnSelectorDirective
  ],
  exports: [
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
})
export class VttTargetModule { }
