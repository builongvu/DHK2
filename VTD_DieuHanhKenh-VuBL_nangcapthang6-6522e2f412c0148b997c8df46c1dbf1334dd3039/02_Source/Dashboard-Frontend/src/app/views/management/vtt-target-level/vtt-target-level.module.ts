import { CUSTOM_ELEMENTS_SCHEMA, NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { VttTargetLevelRoutingModule } from './vtt-target-level.routing.module';
import { VttTargetLevelComponent } from './vtt-target-level.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ButtonsModule, ModalModule, TooltipConfig, TooltipModule } from 'ngx-bootstrap';
import { NgxPaginationModule } from 'ngx-pagination';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { ToastrModule } from 'ngx-toastr';
import { ProductService } from '../../../services/management/product.service';
import { DownloadService } from '../../../services/management/download.service';
import { PlanmonthlyService } from '../../../services/management/planmonthly.service';
import { ChannelService } from '../../../services/management/channel.service';
import { GroupsService } from '../../../services/management/group.service';
import { ServiceService } from '../../../services/management/service.service';
import { UnitStaffService } from '../../../services/management/unitStaff.service';
import { TreeviewModule } from 'ngx-treeview';
import { TranslateModule } from '@ngx-translate/core';
import { ConfigTreeviewModule } from '../../../config-treeview/config-treeview.module';
import { UnitService } from '../../../services/management/unit.service';
import { StaffService } from '../../../services/management/staff.service';
import { MatCheckboxModule, MatIconModule } from '@angular/material';
import { SelectAutocompleteModule } from 'mat-select-autocomplete';
import { DeleteService } from '../../../services/management/delete.service';

@NgModule({
  declarations: [
    VttTargetLevelComponent
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
    VttTargetLevelRoutingModule,
    TooltipModule,
    TreeviewModule.forRoot(),
    TranslateModule,
    ConfigTreeviewModule,
    MatIconModule,
    SelectAutocompleteModule, MatCheckboxModule

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
    UnitService,
    StaffService,
    DeleteService
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
})
export class VttTargetLevelModule { }
