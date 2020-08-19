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
import { WarningSendService } from '../../../services/management/warning-send.service';
import { WarningReceiveService } from '../../../services/management/warning-receive.service';
import { WarningContentService } from '../../../services/management/warning-content.service';
import { MatDialogModule } from '@angular/material/dialog';
import { DashboardModule } from '../../dashboard/dashboard.module';
import { AssignOntimeComponent } from './assign-ontime.component';
import { AssignOntimeRoutingModule } from './assign-ontime.routing.module';

@NgModule({
  declarations: [
    AssignOntimeComponent
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
    AssignOntimeRoutingModule,
    MatTabsModule,
    MatDialogModule,
    DashboardModule
  ],
  providers: [
    WarningReceiveService,
    WarningSendService,
    WarningContentService
  ],
  entryComponents: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
})
export class AssignOntimeModule { }
