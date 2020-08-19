import { DashboardModule } from '../../dashboard/dashboard.module';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ButtonsModule } from 'ngx-bootstrap/buttons';

import { ModalModule } from 'ngx-bootstrap/modal';
import { NgxPaginationModule } from 'ngx-pagination';
import { ProductService } from '../../../services/management/product.service';

import {
  MatDatepickerModule,
  MatFormFieldModule,
  MatInputModule,
  MatPaginatorModule,
  MatTableModule,
  MatDialogModule,
  MatNativeDateModule,
  MatTooltipModule,
  MatCheckboxModule
} from '@angular/material';
import { MatSelectModule } from '@angular/material/select';
import { ToastrModule } from 'ngx-toastr';
import { TooltipConfig, TooltipModule } from 'ngx-bootstrap';
import { TreeviewModule } from 'ngx-treeview';
import { TranslateModule } from '@ngx-translate/core';
import { ConfigTreeviewModule } from '../../../config-treeview/config-treeview.module';
import { MatTabsModule } from '@angular/material/tabs';
import { ObjectRoleRoutingConfig } from './object-role-config.routing';
import { ListRoleComponent } from './list-role/list-role.component';
import { CreateRoleComponent } from './create-role/create-role.component';
import {NgMultiSelectDropDownModule} from 'ng-multiselect-dropdown';
import { EditRoleComponent } from './edit-role/edit-role.component';

@NgModule({
  imports: [
    MatCheckboxModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    FormsModule,
    CommonModule,
    ModalModule.forRoot(),
    ButtonsModule.forRoot(),
    ReactiveFormsModule,
    NgxPaginationModule,
    MatTableModule,
    MatDatepickerModule,
    MatDialogModule,
    ToastrModule.forRoot(),
    TooltipModule,
    TreeviewModule.forRoot(),
    TranslateModule,
    ConfigTreeviewModule,
    MatTabsModule,
    MatNativeDateModule,
    DashboardModule,
    MatTooltipModule,
    ObjectRoleRoutingConfig,
    NgMultiSelectDropDownModule,

  ],
  providers: [
    TooltipConfig,
  ],
  declarations: [
    ListRoleComponent,
    CreateRoleComponent,
    EditRoleComponent
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
  entryComponents: [CreateRoleComponent, EditRoleComponent]
})
export class ObjectRoleConfigModule { }
