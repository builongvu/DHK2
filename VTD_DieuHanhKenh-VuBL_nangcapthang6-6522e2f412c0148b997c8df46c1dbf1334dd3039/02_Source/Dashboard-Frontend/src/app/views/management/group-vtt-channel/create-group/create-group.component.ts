import { WarningReceiveService } from './../../../../services/management/warning-receive.service';
import { DownloadService } from './../../../../services/management/download.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr';
import { GroupVttChannelService } from './../../../../services/management/group-vtt-channel.service';
import { VTTGroupChannel } from './../../../../models/vttgroupchannel.model';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material';
import { Component, OnInit, TemplateRef } from '@angular/core';


@Component({
  selector: 'app-create-group',
  templateUrl: './create-group.component.html',
  styleUrls: ['./create-group.component.scss']
})
export class CreateGroupComponent implements OnInit {

  newRoleForm: FormGroup;
  mobjModalRef: BsModalRef;
  mblnServiceName: boolean;
  mstrServiceCode = '';
  codeGroupConflict: boolean;


  constructor(
    private dialogRef: MatDialogRef<CreateGroupComponent>,
    private groupVttChannelService: GroupVttChannelService,
    private toastr: ToastrService,
    private translate: TranslateService,
    private modalService: BsModalService,
    private warrningreceive: WarningReceiveService,
    private fb: FormBuilder,
  ) { dialogRef.disableClose = true; }

  ngOnInit() {
    this.createRoleFrom();
  }

  createRoleFrom() {
    this.newRoleForm = this.fb.group({
      code: ['', [Validators.required, Validators.pattern('^\\s{0,10}?[_A-Za-z0-9]{1,50}\\s{0,10}$')]],
      name: ['', [Validators.required]],
      status: [1, [Validators.required]]
    });
  }

  hasError(controlName: string, errorName: string) {
    return this.newRoleForm.controls[controlName].hasError(errorName);
  }

  close() {
    this.dialogRef.close();
  }

  save() {
    if (this.mblnServiceName) {
      this.mblnServiceName = true;
      this.mobjModalRef.hide();
      return;
    }

    const vttGroupChannel = new VTTGroupChannel();
    vttGroupChannel.groupChannelCode = this.newRoleForm.get('code').value.toString().trim();
    vttGroupChannel.groupChannelName = this.newRoleForm.get('name').value.toString().trim();
    vttGroupChannel.status = '1';

    this.groupVttChannelService.addVttGroupChannel(vttGroupChannel).subscribe(data => {
      if (data.code === 200) {
        this.showSuccess(this.translate.instant('management.warningconfig.addsuccess'));
        this.mobjModalRef.hide();
        this.warrningreceive.setReloadWarning(1);
      }
    });

    this.createRoleFrom();
    this.mstrServiceCode = '';

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

  checkNameEmpty() {
    if (this.newRoleForm.get('name').value.toString().trim() == '' && this.newRoleForm.get('name').value != '') {
      this.mblnServiceName = true;
    } else {
      this.mblnServiceName = false;
    }
  }

  checkCodeConflict() {
    this.mstrServiceCode = this.mstrServiceCode.toUpperCase();
    const groupCode = this.newRoleForm.get('code').value.toString();
    if (groupCode && groupCode != '') {
      this.groupVttChannelService.checkCodeConfilic(groupCode).subscribe((data: any) => {
        if (data.data === 'DUPLICATED_VTT_GROUP_CHANNEL') {
          this.codeGroupConflict = true;
        } else {
          this.codeGroupConflict = false;
        }
      });
    }
  }
}
