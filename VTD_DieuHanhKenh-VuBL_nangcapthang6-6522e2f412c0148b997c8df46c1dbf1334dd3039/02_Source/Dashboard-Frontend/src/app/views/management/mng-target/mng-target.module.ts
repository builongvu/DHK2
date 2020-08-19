import {TargetCreateComponent} from './target-create/target-create.component';
import {DashboardModule} from './../../dashboard/dashboard.module';
import {ConfigTreeSelectComponent} from './config-tree-select/config-tree-select.component';
import {TargetInfoComponent} from './target-info/target-info.component';
import {MngTargetRoutingModule} from './mng-target.routing';
import {CUSTOM_ELEMENTS_SCHEMA, NgModule, NO_ERRORS_SCHEMA} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {ButtonsModule} from 'ngx-bootstrap/buttons';

import {ModalModule} from 'ngx-bootstrap/modal';
import {NgxPaginationModule} from 'ngx-pagination';
import {ProductService} from '../../../services/management/product.service';

import {ChannelService} from '../../../services/management/channel.service';
import {
  MatDatepickerModule,
  MatFormFieldModule,
  MatInputModule,
  MatPaginatorModule,
  MatTableModule,
  MatDialogModule,
  MatNativeDateModule,
  MatTooltipModule, MatCheckboxModule,
} from '@angular/material';
import {MatSelectModule} from '@angular/material/select';
import {GroupsService} from '../../../services/management/group.service';
import {ServiceService} from '../../../services/management/service.service';
import {ToastrModule} from 'ngx-toastr';
import {TooltipConfig, TooltipModule} from 'ngx-bootstrap';
import {TreeviewModule} from 'ngx-treeview';
import {TranslateModule} from '@ngx-translate/core';
import {ConfigTreeviewModule} from '../../../config-treeview/config-treeview.module';
import {MngTargetComponent} from './list-target/mng-target.component';
import {MatTabsModule} from '@angular/material/tabs';
import {TargetHistoryComponent} from './target-history/target-history.component';
import {TargetDetailComponent} from './target-detail/target-detail.component';
import {TargetWeigthComponent} from './target-weigth/target-weigth.component';
import {ObjectconfigComponent} from '../objectconfig/objectconfig.component';
import {MngWarrningComponent} from './mng-warrning/mng-warning.component';
import {WarningCreateComponent} from './warning-create/warning-create.component';
import {WarningEditComponent} from './warning-edit/warning-edit.component';
import {AddTargetWeigthComponent} from './target-weigth/add-target-weigth/add-target-weigth.component';
import {EditTargetWeigthComponent} from './target-weigth/edit-target-weigth/edit-target-weigth.component';


@NgModule({
  imports: [
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
    MngTargetRoutingModule,
    MatTabsModule,
    MatNativeDateModule,
    DashboardModule,
    MatTooltipModule,
    MatCheckboxModule,
  ],
  providers: [
    ProductService,
    ChannelService,
    GroupsService,
    ServiceService,
    TooltipConfig,
  ],
  declarations: [
    MngTargetComponent,
    ConfigTreeSelectComponent,
    TargetInfoComponent,
    TargetHistoryComponent,
    TargetDetailComponent,
    ObjectconfigComponent,
    TargetCreateComponent,
    MngWarrningComponent,
    TargetWeigthComponent,
    WarningCreateComponent,
    WarningEditComponent,
    AddTargetWeigthComponent,
    EditTargetWeigthComponent
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
  entryComponents: [TargetCreateComponent, WarningCreateComponent, WarningEditComponent, AddTargetWeigthComponent, EditTargetWeigthComponent]
})
export class MngTargetModule {
}
