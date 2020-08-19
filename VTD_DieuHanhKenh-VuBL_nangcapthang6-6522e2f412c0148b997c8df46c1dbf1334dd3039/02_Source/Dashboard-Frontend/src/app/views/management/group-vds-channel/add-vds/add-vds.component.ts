import { Component, OnInit, TemplateRef } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Channel } from '../../../../models/Channel.model';
import { MatDialogRef } from '@angular/material';
import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { VdsService } from '../../../../services/management/vds-service.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { WarningReceiveService } from '../../../../services/management/warning-receive.service';
import { ChannelService } from '../../../../services/management/channel.service';

@Component({
  selector: 'app-add-vds',
  templateUrl: './add-vds.component.html',
  styleUrls: ['./add-vds.component.scss']
})
export class AddVdsComponent implements OnInit {

  form;
  mobjModalRef: BsModalRef;
  mobjChannel: Channel = new Channel();
  marrVDSGroup: Channel[] = [];
  spaceName: boolean = false;
  mblnCodeDuplicate: boolean = false;
  constructor(
    private fb: FormBuilder,
    private modalService: BsModalService,
    private dialogRef: MatDialogRef<AddVdsComponent>,
    private toastr: ToastrService,
    private translate: TranslateService,
    private vdsService: VdsService,
    private warningService: WarningReceiveService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit() {
    this.mobjChannel.status = 1;
    this.createForm();
    this.getVDSGroup();
  }
  createForm() {
    this.form = this.fb.group(
      {
        code: ['', [Validators.required, Validators.pattern('^\\s{0,50}?[_A-Za-z0-9]{1,50}\\s{0,50}$')]],
        name: ['', Validators.required],
      });
  }

  getVDSGroup() {
    this.vdsService.getAllVdsChannel().subscribe(res => {
      this.marrVDSGroup = res.data;
    });
  }

  checkCode() {
    if (this.mobjChannel.code && this.mobjChannel.code.trim().length > 0) {
      this.mobjChannel.code = this.mobjChannel.code.toUpperCase();
      let mobj = this.marrVDSGroup.filter(item => item.code == this.mobjChannel.code.trim());
      if (mobj.length>0) {
        this.mblnCodeDuplicate = true;
      } else {
        this.mblnCodeDuplicate = false;
      }
    } else {
      this.mblnCodeDuplicate = false;
    }
  }

  checkname(){
    if(this.mobjChannel.name){
      if(this.mobjChannel.name.trim().length == 0){
        this.spaceName = true;
      }else{
        this.spaceName = false;
      }
    }else{
      this.spaceName = false;
    }
  }

  hasError(controlName: string, errorName: string) {
    return this.form.controls[controlName].hasError(errorName);
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
    if(!this.form.valid || this.mblnCodeDuplicate != false || this.spaceName != false){
      return;
    }
    this.mobjChannel.code = this.mobjChannel.code.trim();
    this.mobjChannel.name = this.mobjChannel.name.trim();
    this.vdsService.addVDSGroup(this.mobjChannel).subscribe(res => {
      if (res.code === 200) {
        this.showSuccess(this.translate.instant('management.warningconfig.addsuccess'));
        this.createForm();
        this.getVDSGroup();
        this.warningService.setReload(null);
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
