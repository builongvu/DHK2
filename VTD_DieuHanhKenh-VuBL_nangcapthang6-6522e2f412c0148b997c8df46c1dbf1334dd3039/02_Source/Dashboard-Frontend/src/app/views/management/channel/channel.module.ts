import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
// import { ChartsModule } from 'ng2-charts/ng2-charts';
// import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { ButtonsModule } from 'ngx-bootstrap/buttons';

import { ChannelComponent } from './channel.component';
import { CEChannelComponent } from './create.edit.channel.component';
import { ChannelRoutingModule } from './channel-routing.module';
import { ModalModule } from 'ngx-bootstrap/modal';
import { NgxPaginationModule } from 'ngx-pagination';
import {ChannelService} from '../../../services/management/channel.service';
import {TranslateModule} from '@ngx-translate/core';

@NgModule({
  imports: [
    FormsModule,
    ChannelRoutingModule,
    CommonModule,
    ModalModule.forRoot(),
    ButtonsModule.forRoot(),
    ReactiveFormsModule,
    TranslateModule,
    NgxPaginationModule
  ],
  providers: [ChannelService],
  declarations: [ChannelComponent, CEChannelComponent]
})
export class ChannelModule { }
