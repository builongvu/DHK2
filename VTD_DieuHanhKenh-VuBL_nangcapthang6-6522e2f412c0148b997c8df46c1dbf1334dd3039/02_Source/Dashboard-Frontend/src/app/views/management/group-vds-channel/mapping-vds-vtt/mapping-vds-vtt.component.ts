import { Component, OnInit, TemplateRef, Inject } from '@angular/core';
import { VdsService } from '../../../../services/management/vds-service.service';
import { TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { BsModalService, BsModalRef } from 'ngx-bootstrap';
import { TreeviewItem, TreeviewConfig } from 'ngx-treeview';
import { MappingGroupChannel } from '../../../../models/mappinggroupchannel.model';
import { MappinggroupchannelService } from '../../../../services/management/mappinggroupchannel.service';
import { warningservice } from '../../../../models/warningservice';
import { WarningReceiveService } from '../../../../services/management/warning-receive.service';
import { VttgroupchannelService } from '../../../../services/management/vttgroupchannel.service';
import { config } from '../../../../config/application.config';

@Component({
  selector: 'app-mapping-vds-vtt',
  templateUrl: './mapping-vds-vtt.component.html',
  styleUrls: ['./mapping-vds-vtt.component.scss']
})
export class MappingVdsVttComponent implements OnInit {


  mobjModalRef: BsModalRef;
  valueVDS;
  marrNodeVDS: TreeviewItem[] = [];
  marrNodeVTT: TreeviewItem[] = [];
  valueVTT;
  checkVDS: number;
  checkVTT: number;
  added = false;
  loaded;
  open;
  mobjMapping: MappingGroupChannel;
  config = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 80,
  });
  marrVTTGroupChannel;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private modalService: BsModalService,
    private dialogRef: MatDialogRef<MappingVdsVttComponent>,
    private toastr: ToastrService,
    private translate: TranslateService,
    private vdsService: VdsService,
    private mappingService: MappinggroupchannelService,
    private warningService: WarningReceiveService,
    private vttService: VttgroupchannelService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit() {
    this.mobjMapping = new MappingGroupChannel();
    this.mobjMapping.id = -1;
    if (this.data.type == 1) {
      this.loadData();
      this.checkVTT = 0;
      this.checkVDS = 0;
    } else {
      this.getVTTChannel();
    }
  }

  getVTTChannel() {
    let code = '';
    if (this.data.type == 1) {
      code = this.data.mobjMap.groupChannelCode;
    } else {
      code = this.data.mobjMap.groupChannelCode;
    }
    this.vttService.getAllNotMapping(code).subscribe(
      next => {
        this.marrNodeVTT = [];
        this.marrVTTGroupChannel = next.data;
        const channel = new MappingGroupChannel();
        if (localStorage.getItem(config.user_default_language) === 'en') {
          channel.groupChannelName = 'Select';
        } else {
          channel.groupChannelName = 'Chọn';
        }
        channel.groupChannelCode = 'null';
        this.marrVTTGroupChannel.reverse();
        this.marrVTTGroupChannel.push(channel);
        this.marrVTTGroupChannel.reverse();
        this.marrVTTGroupChannel.forEach(
          valueUnit => {
            const text = valueUnit['groupChannelName'];
            const value = valueUnit['groupChannelCode'];
            this.marrNodeVTT.push(new TreeviewItem({ text, value, checked: false }));
          }
        );
        this.valueVTT = 'null';
        if (this.data.type == 1) {
          this.loadData();
        } else {
          this.marrNodeVDS = this.data.marrVDS;
          this.mobjMapping = this.data.mobjMap;
          this.loaded = true;
          // this.valueVDS = this.mobjMapping.vdsChannelCode;
          this.valueVTT = this.mobjMapping.groupChannelCode;
          this.valueVDS = 'null';
        }
      },
      error => (this.marrNodeVTT = [], console.log(error))
    );
  }

  loadData() {
    this.mappingService.getById(this.data.mobjMap.id).subscribe(res => {
      this.mobjMapping = res.data;
      this.loaded = true;
      this.marrNodeVDS = this.data.marrVDS;
      this.valueVDS = this.mobjMapping.vdsChannelCode;
    });
  }

  OnchangeVDS(value: any) {
    this.mobjMapping.vdsChannelCode = value;
    if (!this.added && this.open) {
      this.checkSelectVDS();
    }
  }

  OnchangeVTT(value: any) {
    this.mobjMapping.groupChannelCode = value;
    if (!this.added) {
      this.checkSelectVTT();
    }
  }

  checkSelectVDS() {
    this.added = false;
    this.open = 1;
    if ( this.mobjMapping.vdsChannelCode == 'null') {
      this.checkVDS = 1;
    } else {
      this.checkVDS = 0;
    }
  }

  checkSelectVTT() {
    this.added = false;
    if (this.loaded == true) {
      if (!this.mobjMapping.groupChannelCode || this.mobjMapping.groupChannelCode == 'null') {
        this.checkVTT = 1;
      } else {
        this.checkVTT = 0;
      }
    }
  }

  showConfirm(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }
  back() {
    this.mobjModalRef.hide();
  }

  save() {
    this.mobjMapping.vdsChannelName = this.marrNodeVDS.filter(item => item.value == this.mobjMapping.vdsChannelCode)[0].text;
    if(this.data.type != 1){
      this.mobjMapping.groupChannelName = this.marrNodeVTT.filter(item => item.value == this.mobjMapping.groupChannelCode)[0].text;
    }
    if (this.mobjMapping.id == -1) {
      this.mobjMapping.id = null;
    }
    this.mappingService.addMapping(this.mobjMapping).subscribe(res => {
      if (res.code == 200) {
        if (res.data == 'DUPLICATED_VDS_GROUP_CHANNEL') {
          this.showError(this.translate.instant('management.VDSGroup.duplicate'));
        } else {
          this.valueVTT = 'null';
          this.valueVDS = 'null';
          this.added = true;
          this.open = null;
          this.checkVDS = null;
          this.checkVTT = null;
          if (this.mobjMapping.id == null) {
            this.warningService.setReloadWarning(1);
            this.getVTTChannel();
            this.showSuccess(this.translate.instant('management.warningconfig.addsuccess'));
          } else {
            this.warningService.setReloadWarning(0);
            this.showSuccess(this.translate.instant('management.warningconfig.updatesuccess'));
            this.close();
          }
        }
      } else {
        this.showError(res.errors);
      }
      this.back();
    });
  }

  /**
 * Thông báo thành công
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  showSuccess(message: string) {
    this.toastr.success(message,
      this.translate.instant('management.warningconfig.success'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }

  /**
  * Thông báo thất bại(Lỗi)
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2019/11/18
  */
  showError(message: string) {
    this.toastr.error(message,
      this.translate.instant('management.warningconfig.fail'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }

  close() {
    this.dialogRef.close();
  }
}
