import { WarningReceiveService } from './../../../../services/management/warning-receive.service';
import { VttGroupChannelSale } from './../../../../models/VttGroupChannelSale.model';
import { Component, OnInit, TemplateRef } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { MatDialogRef } from '@angular/material';
import { CreateGroupComponent } from '../create-group/create-group.component';
import { GroupVttChannelService } from '../../../../services/management/group-vtt-channel.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { VTTGroupChannel } from '../../../../models/vttgroupchannel.model';
import { VttPosition } from '../../../../models/VttPosition.model';
import { PositionService } from '../../../../services/management/position.service';
import { TreeviewItem, TreeviewConfig } from 'ngx-treeview';
import { Position } from '../../../../models/Positon.model';
import { config } from '../../../../config/application.config';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { DownloadService } from '../../../../services/management/download.service';
import * as fileSaver from 'file-saver';

@Component({
  selector: 'app-create-group-shop',
  templateUrl: './create-group-shop.component.html',
  styleUrls: ['./create-group-shop.component.scss']
})
export class CreateGroupShopComponent implements OnInit {

  newRoleForm: FormGroup;
  mobjModalRef: BsModalRef;
  mblnServiceName: boolean;
  mstrServiceCode = '';
  marrPosition: PositionService[] = [];
  marrVTTGroupChannel: VTTGroupChannel[] = [];
  marrChannelVTTNew: TreeviewItem[] = [];
  valueVttChannel: any;

  marrPositions: Position[] = [];
  marrPositionsTree: TreeviewItem[] = [];
  valuePosition: any;

  mobjConfig = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 80,
  });
  mnbrChannel: any = 'null';

  mnbrCode;
  mblnIsSelectFile = false;
  mblnCheckFileNull = false;
  mobjFileList: FileList;
  mobjDataUpload;
  mstrMessageUpload;
  mnbrSumSuccessfulRecord;
  mnbrSumRecord;
  mstrResultFileName;
  mblnIsClickHere = false;
  vblnCheckChannel: boolean;
  checkChannelError: number;
  channelIDLessThanZero: boolean;
  
  constructor(
    private dialogRef: MatDialogRef<CreateGroupComponent>,
    private groupVttChannelService: GroupVttChannelService,
    private positionService: PositionService,
    private toastr: ToastrService,
    private translate: TranslateService,
    private modalService: BsModalService,
    private warrningreceive: WarningReceiveService,
    private http: HttpClient,
    private downloadService: DownloadService,
    private fb: FormBuilder) { dialogRef.disableClose = true; }

  ngOnInit() {
    this.createRoleFrom();
    this.getChannels();
    this.checkChannelError = 1;
  }

  getChannels() {
    this.groupVttChannelService.getActiveVttChannel().subscribe(
      (vobjNext: any) => {
        this.marrVTTGroupChannel = vobjNext.data;
        const vttGroupChannel = new VTTGroupChannel();
        vttGroupChannel.groupChannelCode = 'null';
        if (localStorage.getItem(config.user_default_language) === 'en') {
          vttGroupChannel.groupChannelName = 'Select';
        } else {
          vttGroupChannel.groupChannelName = 'Chọn';
        }
        this.marrVTTGroupChannel.reverse();
        this.marrVTTGroupChannel.push(vttGroupChannel);
        this.marrVTTGroupChannel.reverse();
        if (vobjNext) {
          this.marrVTTGroupChannel.forEach(
            valueUnit => {
              const text = valueUnit['groupChannelName'];
              const value = valueUnit['groupChannelCode'];
              this.marrChannelVTTNew.push(new TreeviewItem({ text, value, checked: false }));
            }
          );
        }
      },
      error => (this.marrChannelVTTNew = [])
    );
  }

  createRoleFrom() {
    this.newRoleForm = this.fb.group({
      channelTypeId: ['', [Validators.required, Validators.pattern('^\\s{0,2}?[0-9]{1,8}\\s{0,2}$'), Validators.min(1)]],
    });
  }


  hasError(controlName: string, errorName: string) {
    return this.newRoleForm.controls[controlName].hasError(errorName);
  }

  close() {
    this.dialogRef.close();
  }

  onChange(value: any) {
    this.mnbrChannel = value;
    this.checkChannel();
  }

  save() {
    if (this.mnbrChannel == null) {
      this.mobjModalRef.hide();
      return;
    }

    const vttGroupChannelSale = new VttGroupChannelSale();
    vttGroupChannelSale.groupChannelCode = this.mnbrChannel;
    vttGroupChannelSale.channelTypeId = Number(this.newRoleForm.get('channelTypeId').value.toString().trim());

    this.groupVttChannelService.addVttGroupChannelSale(vttGroupChannelSale).subscribe(data => {
      if (data.code === 200) {
        if (data.data === 'DUPLICATED_VTT_GROUP_CHANNEL_SALE') {
          this.showError(this.translate.instant('management.groupvttchannel.recordPosition'));
          this.mobjModalRef.hide();
        } else {
          this.showSuccess(this.translate.instant('management.warningconfig.addsuccess'));
          this.mobjModalRef.hide();
          this.mnbrChannel = 'null';
          this.checkChannelError = 0;
          this.createRoleFrom();
          this.warrningreceive.setReloadWarning(1);
        }
      }
    });
  }
  showSuccess(message: string) {
    this.toastr.success(message,
      this.translate.instant('management.service.message.success'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }

  showError(message: string) {
    this.toastr.error(message,
      this.translate.instant('management.service.message.fail'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }

  onBackConfirm() {
    this.mobjModalRef.hide();
  }

  confirmEdit(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }

  checkChannel() {
    if (this.checkChannelError === 1) {
      if (this.mnbrChannel === 'null') {
        this.vblnCheckChannel = true;
      } else {
        this.vblnCheckChannel = false;
      }
    }
    this.checkChannelError = 1;
  }

  checkCodeConflict() {
    this.mstrServiceCode = this.mstrServiceCode.toUpperCase();
  }

  checkChannelType() {
    const channelID = Number(this.newRoleForm.get('channelTypeId').value.toString().trim());
    if (channelID === 0) {
      this.channelIDLessThanZero = true;
    } else {
      this.channelIDLessThanZero = false;
    }
  }

  upLoad() {
    this.mblnIsSelectFile = true;
    if (this.mobjFileList) {
      const vfileFile: File = this.mobjFileList[0];
      const vobjFormData: FormData = new FormData();
      vobjFormData.append('file', vfileFile);
      const vobjHeaders = new HttpHeaders();
      vobjHeaders.append('Content-type', 'multipart/form-data');
      vobjHeaders.append('Accept', 'application/json');
      const vobjOptions = { headers: vobjHeaders };
      this.http.post(config.uploadVttGroupChannelSale_API, vobjFormData, vobjOptions)
        .subscribe(
          vobjData => {
            this.resultUpload(vobjData);
            this.warrningreceive.setReloadWarning(1);
          },
          vobjError => (console.log(vobjError)),
        );
    } else {
      this.mblnCheckFileNull = true;
    }

    return this.mobjDataUpload;
  }

  selectFile(pobjEvent) {
    this.mnbrCode = null;
    this.mblnIsSelectFile = false;
    if (!this.mobjFileList) {
      this.mblnIsSelectFile = true;
    } else if (this.mobjFileList[0] !== pobjEvent.target.files[0]) {
      this.mblnIsSelectFile = false;
    }
    this.mobjFileList = pobjEvent.target.files;
    this.mblnCheckFileNull = false;
  }

  resultUpload(pobjData) {
    this.mobjDataUpload = pobjData['data'];
    this.mnbrCode = pobjData.code;
    this.mstrMessageUpload = this.mobjDataUpload['message'];
    if (this.mnbrCode === 200) {
      this.mnbrSumSuccessfulRecord = this.mobjDataUpload.sumSuccessfulRecord;
      this.mnbrSumRecord = this.mobjDataUpload.sumRecord;
      this.mstrResultFileName = this.mobjDataUpload.resultFileName;
      if (this.mnbrSumSuccessfulRecord > 0) {
        // this.showSuccess(this.translate.instant('management.service.message.successImport'));
      }
    } else {
      // this.showError(this.translate.instant('management.service.message.failImport'));
      this.mobjFileList = null;
    }
    this.mblnIsSelectFile = true;
  }

  downloadResult(pstrNameFileResult: string) {
    this.mblnIsClickHere = true;
    let vstFfilename: string;
    this.http.get(`${config.download_Result_File_API}?fileName=${pstrNameFileResult}`);
    this.downloadService.downloadResult(pstrNameFileResult)
      .subscribe(vobjResponse => {
        vstFfilename = this.mstrResultFileName;
        this.saveFile(vobjResponse.body, vstFfilename);
      });
  }

  saveFile(pobjData: any, pstrFilename?: string) {
    const vobjBlob = new Blob([pobjData], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    fileSaver.saveAs(vobjBlob, pstrFilename);
  }

  onBack() {
    this.mblnIsClickHere = false;
    this.mnbrCode = null;
    this.mobjFileList = null;
    this.mblnCheckFileNull = false;
    this.mobjModalRef.hide();
  }

  confirm(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }


}
