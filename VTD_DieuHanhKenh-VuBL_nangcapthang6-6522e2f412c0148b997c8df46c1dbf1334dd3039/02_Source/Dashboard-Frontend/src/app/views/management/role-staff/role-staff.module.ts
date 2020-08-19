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
import {ToastrModule} from 'ngx-toastr';
import {TreeviewModule} from 'ngx-treeview';
import {TranslateModule} from '@ngx-translate/core';
import {MatIconModule} from '@angular/material';
import {MatTabsModule} from '@angular/material/tabs';
import {MatDialogModule} from '@angular/material/dialog';
import { RoleStaffComponent } from './role-staff.component';
import { RoleStaffRoutingModule } from './role-staff.routing.module';
import { RoleStaffService } from '../../../services/management/role-staff.service';
import { ShopService } from '../../../services/management/shop.service';
@NgModule({
  declarations: [
    RoleStaffComponent
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
    RoleStaffRoutingModule,
    MatTabsModule,
    MatDialogModule,
  ],
  providers: [
    RoleStaffService,
  ],
  entryComponents: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
})
export class RoleStaffModule { }
