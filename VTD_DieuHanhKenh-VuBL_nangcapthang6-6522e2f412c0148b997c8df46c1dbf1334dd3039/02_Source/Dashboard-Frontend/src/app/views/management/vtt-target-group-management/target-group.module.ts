import {CUSTOM_ELEMENTS_SCHEMA, NgModule, NO_ERRORS_SCHEMA} from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
// import { ChartsModule } from 'ng2-charts/ng2-charts';
// import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { ButtonsModule } from 'ngx-bootstrap/buttons';

import { ModalModule } from 'ngx-bootstrap/modal';
import { NgxPaginationModule } from 'ngx-pagination';
// import { AppTranslationModule } from '../../../app.translation.module';
import { ProductService } from '../../../services/management/product.service';
import { DownloadService } from '../../../services/management/download.service';
// import { PlanmonthlyService } from '../../../services/management/planmonthly.service';
// import { DatePipe } from '../../../services/management/date.pipe';
// import { ChannelService } from '../../../services/management/channel.service';
// import {GroupsService} from '../../../services/management/group.service';
// import {ProductServiceService} from '../../../services/management/product-service.service';
import { MatSelectModule } from '@angular/material/select';
import {GroupsService} from '../../../services/management/group.service';
// import {ServiceService} from '../../../services/management/service.service';
import {ToastrModule} from 'ngx-toastr';
import {TooltipConfig, TooltipModule} from 'ngx-bootstrap';
// import {FilterPipe} from "../../../services/management/filter.pipe";
import { TranslateModule } from '@ngx-translate/core';
import { VttTargetGroupManagementComponent } from './vtt-target-group-management.component';
import { VttTargetRoutingModule } from '../vtt-target/vtt-target.routing.module';
import { VttTargetManagementRouting } from './vtt-target-group-management.routing.module';
import {MatFormFieldModule, MatInputModule, MatTableModule, MatPaginatorModule, MatDialogModule} from '@angular/material';
import { WarningConfigModule } from '../warning-config/warning-config.module';
import {DialogGroupComponent} from './dialog-group/dialog-group.component';
import {DialogWarningReceiveComponent} from '../warning-config/dialog-warning-receive/dialog-warning-receive.component';

@NgModule({
  imports: [
    MatFormFieldModule, MatInputModule,
    MatSelectModule,
    MatDialogModule,
    FormsModule,
    CommonModule,
    ModalModule.forRoot(),
    ButtonsModule.forRoot(),
    ReactiveFormsModule,
    // AppTranslationModule,
    TranslateModule,
    NgxPaginationModule,
    VttTargetManagementRouting,
    MatTableModule,
    MatPaginatorModule,
    WarningConfigModule,
    // MatDatepickerModule,
    ToastrModule.forRoot(),
    TooltipModule
  ],
  providers: [
    ProductService,
    DownloadService,
    // PlanmonthlyService,
    // ChannelService,
    GroupsService,
    // ServiceService,
    TooltipConfig,
  ],
  declarations: [
    DialogGroupComponent
    // DatePipe,
    // FilterPipe
  ],
  entryComponents: [DialogGroupComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
})
export class TargetGroupModule { }
