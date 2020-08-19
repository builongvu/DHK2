import {CUSTOM_ELEMENTS_SCHEMA, NgModule, NO_ERRORS_SCHEMA} from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatSelectModule } from '@angular/material/select';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {NgMultiSelectDropDownModule} from 'ng-multiselect-dropdown';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ButtonsModule, ModalModule, TooltipModule} from 'ngx-bootstrap';
import {NgxPaginationModule} from 'ngx-pagination';
import {MatTableModule} from '@angular/material/table';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatDatepickerModule} from '@angular/material/datepicker';
import { DomSanitizer } from '@angular/platform-browser';
import {ToastrModule} from 'ngx-toastr';
import {TreeviewModule} from 'ngx-treeview';
import {TranslateModule} from '@ngx-translate/core';
import {MatIconModule} from '@angular/material';
import {MatTabsModule} from '@angular/material/tabs';
import {MatDialogModule} from '@angular/material/dialog';
import { DashboardModule } from '../../dashboard/dashboard.module';
import { ObjectConfigRoutingModule } from './objectconfig.routing.module';
import { ConfigobjectService } from '../../../services/management/configobject.service';
import { MngTargetModule } from '../mng-target/mng-target.module';
import { DialogObjectComponent } from './dialog-object/dialog-object.component';
@NgModule({
  declarations: [
  ],
  imports: [
    MatFormFieldModule, MatInputModule,
    MatSelectModule,
    NgMultiSelectDropDownModule.forRoot(),
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
    ObjectConfigRoutingModule,
    MatTabsModule,
    MatDialogModule,
    DashboardModule,
    MngTargetModule
  ],
  providers: [
    ConfigobjectService
  ],
  entryComponents: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
})
export class ObjectConfigModule { }
