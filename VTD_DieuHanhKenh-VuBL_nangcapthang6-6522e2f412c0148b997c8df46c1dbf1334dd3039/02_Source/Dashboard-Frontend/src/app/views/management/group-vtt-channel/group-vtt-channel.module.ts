import { CreateGroupShopComponent } from './create-group-shop/create-group-shop.component';
import { WarningConfigModule } from './../warning-config/warning-config.module';
import { GroupVttRoutingConfig } from './group-vtt-channel.routing';
import { ListGroupVttChannelComponent } from './list-group-vtt-channel/list-group-vtt-channel.component';
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
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { CreateGroupComponent } from './create-group/create-group.component';
import { CreateGroupPoisitionComponent } from './create-group-poisition/create-group-poisition.component';

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
    GroupVttRoutingConfig,
    NgMultiSelectDropDownModule,
    WarningConfigModule

  ],
  providers: [
    TooltipConfig,
  ],
  declarations: [CreateGroupComponent, CreateGroupShopComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
  entryComponents: [CreateGroupComponent, CreateGroupShopComponent],
})
export class GroupVttChannelModule { }
