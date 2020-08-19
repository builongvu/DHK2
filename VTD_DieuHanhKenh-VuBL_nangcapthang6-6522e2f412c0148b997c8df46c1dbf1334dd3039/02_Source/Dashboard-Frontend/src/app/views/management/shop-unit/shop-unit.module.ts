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
import { ShopService } from '../../../services/management/shop.service';
import { ShopUnitRoutingModule } from './shop-unit.routing.module';
import { DashboardModule } from '../../dashboard/dashboard.module';
import { ShopunitService } from '../../../services/management/shopunit.service';
import { VdschannelService } from '../../../services/management/vdschannel.service';
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
    ShopUnitRoutingModule,
    MatTabsModule,
    MatDialogModule,
    DashboardModule
  ],
  providers: [
    ShopunitService,
    VdschannelService
  ],
  entryComponents: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
})
export class ShopUnitModule { }
