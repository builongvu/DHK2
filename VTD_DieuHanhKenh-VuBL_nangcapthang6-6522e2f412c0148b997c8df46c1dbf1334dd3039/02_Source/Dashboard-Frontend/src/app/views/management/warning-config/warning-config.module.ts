import { CreateGroupPoisitionComponent } from './../group-vtt-channel/create-group-poisition/create-group-poisition.component';
import { ListGroupVttChannelComponent } from './../group-vtt-channel/list-group-vtt-channel/list-group-vtt-channel.component';
import { DetailsReportComponent } from './../details-report/details-report.component';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ButtonsModule, ModalModule, TooltipModule } from 'ngx-bootstrap';
import { NgxPaginationModule } from 'ngx-pagination';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { ToastrModule } from 'ngx-toastr';
import { TreeviewModule } from 'ngx-treeview';
import { TranslateModule } from '@ngx-translate/core';
import { MatIconModule } from '@angular/material';
import { MatTabsModule } from '@angular/material/tabs';
import { WarningConfigComponent } from './warning-config.component';
import { WarningConfigRoutingModule } from './warning-config.routing.module';
import { WarningSendService } from '../../../services/management/warning-send.service';
import { WarningReceiveService } from '../../../services/management/warning-receive.service';
import { WarningReceiveConfigComponent } from './warning-receive-config.component';
import { WarningContentService } from '../../../services/management/warning-content.service';
import { DialogWarningReceiveComponent } from './dialog-warning-receive/dialog-warning-receive.component';
import { MatDialogModule } from '@angular/material/dialog';
import { DashboardModule } from '../../dashboard/dashboard.module';
import { ConfigTreeWRComponent } from './togger/config-tree/config-tree.component';
import { DialogSendComponent } from './dialog-send/dialog-send.component';
import { VttTargetGroupManagementComponent } from '../vtt-target-group-management/vtt-target-group-management.component';
import { GroupVdsChannelComponent } from '../group-vds-channel/group-vds-channel.component';
import { AddVdsComponent } from '../group-vds-channel/add-vds/add-vds.component';
import { MappingVdsVttComponent } from '../group-vds-channel/mapping-vds-vtt/mapping-vds-vtt.component';

@NgModule({
  declarations: [
    WarningReceiveConfigComponent,
    DialogWarningReceiveComponent,
    WarningConfigComponent,
    ConfigTreeWRComponent,
    DialogSendComponent,
    DetailsReportComponent,
    VttTargetGroupManagementComponent,
    ListGroupVttChannelComponent,
    CreateGroupPoisitionComponent,
    GroupVdsChannelComponent,
    AddVdsComponent,
    MappingVdsVttComponent
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
    TooltipModule,
    TreeviewModule.forRoot(),
    TranslateModule,
    MatIconModule,
    WarningConfigRoutingModule,
    MatTabsModule,
    MatDialogModule,
    DashboardModule,
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
    MatTabsModule,
    DashboardModule
  ],
  providers: [
    WarningReceiveService,
    WarningSendService,
    WarningContentService
  ],
  entryComponents: [DialogWarningReceiveComponent, DialogSendComponent,AddVdsComponent, MappingVdsVttComponent, CreateGroupPoisitionComponent],
  exports: [
    ConfigTreeWRComponent
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
})
export class WarningConfigModule { }
