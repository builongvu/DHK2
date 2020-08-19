import { Component, OnInit, Inject, TemplateRef } from '@angular/core';
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr';
import { WarningSendService } from '../../../../services/management/warning-send.service';
import { WarningReceiveService } from '../../../../services/management/warning-receive.service';
import { TreeItem, TreeviewItem, TreeviewConfig } from 'ngx-treeview';
import { WarningSend } from '../../../../models/Warning-config';
import { WarningContent } from '../../../../models/WarningContent';
import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { apParam } from '../../../../models/apParam.model';

@Component({
  selector: 'app-dialog-send',
  templateUrl: './dialog-send.component.html',
  styleUrls: ['./dialog-send.component.scss']
})
export class DialogSendComponent implements OnInit {

  mblnNewEmail: boolean;
  mblnNewSms: boolean;
  mblnDuplicate: boolean;
  vblnCheckService: boolean;
  vblnCheckWarninglv: boolean;
  vblnCheckInformlv: boolean;
  vblnCheckSmsemail: boolean;
  vblnCheckContent: boolean;
  mnbrNewContent: number;
  mnbrNewStatus: number;
  mnbrNewInformLv: number;
  mnbrNewWarningLv: number
  marrInformlevels: apParam[] = [];
  warninglv: any;
  value = -2;
  indexContent = 1;
  mobjModalRef: BsModalRef;
  invalid = 0;
  mnbrNewServiceId;
  marrWarninglevels: apParam[] = [];
  marrWarningContent: WarningContent[] = [];
  mobjNewWarning: WarningSend;
  marrNodeTreeviewServices: TreeviewItem[] = [];
  index = 1;
  Service = "";
  config = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 110,
  });
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private warningSendService: WarningSendService,
    private toastr: ToastrService,
    private translate: TranslateService,
    public dialog: MatDialog,
    private dialogRef: MatDialogRef<DialogSendComponent>,
    private modalService: BsModalService,
    private warningReceiveService: WarningReceiveService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit() {
    this.marrNodeTreeviewServices = this.data.marr;
    this.mobjNewWarning = this.data.warning;
    this.marrWarningContent = this.data.content.filter(item => item.mstrStatus === "1");
    this.marrInformlevels = this.data.inform.filter(item => item.status === "1");
    this.marrWarninglevels = this.data.warninglv.filter(item => item.status === "1");
    this.invalid = 0;
    this.mobjNewWarning = this.data.warning;
    if (this.mobjNewWarning.mlngId === -1) {
      this.newData();
    } else {
      this.vblnCheckService = false;
      this.vblnCheckWarninglv = false;
      this.vblnCheckInformlv = false;
      this.vblnCheckSmsemail = false;
      this.Service = this.data.service;
      this.vblnCheckContent = false;
      if (this.mobjNewWarning.mintEmail === 1) {
        this.mblnNewEmail = true;
      } else {
        this.mblnNewEmail = false;
      }
      if (this.mobjNewWarning.mintSms === 1) {
        this.mblnNewSms = true;
      } else {
        this.mblnNewSms = false;
      }
      this.mnbrNewContent = -1;
      this.mnbrNewWarningLv = -1;
      this.mnbrNewInformLv = -1;
      for (let i = 0; i < this.marrWarninglevels.length; i++) {
        if (this.marrWarninglevels[i].code === this.mobjNewWarning.mintWarningLevel + "") {
          this.mnbrNewWarningLv = this.mobjNewWarning.mintWarningLevel;
        }
      };
      for (let i = 0; i < this.marrInformlevels.length; i++) {
        if (this.marrInformlevels[i].code === this.mobjNewWarning.mintInformLevel + "") {
          this.mnbrNewInformLv = this.mobjNewWarning.mintInformLevel;
        }
      };
      for (let i = 0; i < this.marrWarningContent.length; i++) {
        if (this.marrWarningContent[i].mlngId === parseInt(this.mobjNewWarning.mlngIdContent)) {
          this.mnbrNewContent = parseInt(this.mobjNewWarning.mlngIdContent);
        }
      };
      this.value = this.mobjNewWarning.mlngServiceId;
    }
  }

  close(){
    this.dialogRef.close();
  }

  confirm(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  } 
  back() {
    this.mobjModalRef.hide();
  }

  newData() {
    this.mobjNewWarning = new WarningSend();
    this.marrNodeTreeviewServices = [];
    this.marrNodeTreeviewServices = this.data.marr;
    this.value = -2;
    this.mblnNewEmail = false;
    this.mblnNewSms = false;
    this.mnbrNewContent = -1;
    this.mnbrNewServiceId = null;
    this.mnbrNewWarningLv = -1;
    this.mnbrNewInformLv = -1;
    this.mobjNewWarning.mstrStatus = "1";
    this.mobjNewWarning.mlngId = -1;
    this.mobjNewWarning.mlngServiceId = -2;
    this.vblnCheckService = null;
    this.vblnCheckWarninglv = null;
    this.vblnCheckInformlv = null;
    this.vblnCheckSmsemail = null;
    this.vblnCheckContent = null;
    this.index = 1;
    this.indexContent = 1;
  }

  checkService() {
    if (this.mobjNewWarning.mlngServiceId === -2) {
      if (this.index === 1) {
      }else{
        this.vblnCheckService = true;
      }
      this.index = 2;
    } else {
      this.vblnCheckService = false;
    }
  }
  checkWarninglv() {
    if (this.mnbrNewWarningLv === -1) {
      this.vblnCheckWarninglv = true;
    } else {
      this.vblnCheckWarninglv = false;
    }
  }
  checkInformlv() {
    if (this.mnbrNewInformLv === -1) {
      this.vblnCheckInformlv = true;
    } else {
      this.vblnCheckInformlv = false;
    }
  }
  checkSmsemail() {
    if (this.mblnNewSms === false && this.mblnNewEmail === false) {
      this.vblnCheckSmsemail = true;
    } else {
      this.vblnCheckSmsemail = false;
    }
  }

  checkContent() {
    if (this.mnbrNewContent === -1) {
      if(this.indexContent !== 1){
      this.vblnCheckContent = true;
      }
      this.indexContent = 2;
    } else {
      this.vblnCheckContent = false;
    }
  }

  /**
 * Thêm mới/Cập nhật cấu hình gửi cảnh báo
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  SaveAdd() {
    this.mobjNewWarning.mintWarningLevel = this.mnbrNewWarningLv;
    this.mobjNewWarning.mintInformLevel = this.mnbrNewInformLv;
    this.mobjNewWarning.mlngIdContent = this.mnbrNewContent + "";
    if (this.mblnNewSms === true) {
      this.mobjNewWarning.mintSms = 1;
    } else {
      this.mobjNewWarning.mintSms = 0;
    }
    if (this.mblnNewEmail === true) {
      this.mobjNewWarning.mintEmail = 1;
    } else {
      this.mobjNewWarning.mintEmail = 0;
    }
    this.warningSendService.saveWarningSend(this.mobjNewWarning).subscribe(res => {
      if (res.code === 500) {
        this.showError(this.translate.instant('management.warningconfig.duplicate'));
      } else {
        if (this.mobjNewWarning.mlngId === -1) {
          this.newData();
          this.warningReceiveService.setReloadWarning(1);
          this.showSuccess(this.translate.instant('management.warningconfig.addsuccess'));
        } else {
          this.showSuccess(this.translate.instant('management.warningconfig.updatesuccess'));
          this.warningReceiveService.setReloadWarning(0);
          this.dialogRef.close();
        }
      }
    }, error => {
      this.showError(this.translate.instant('management.warningconfig.error'));
    });
    this.mobjModalRef.hide();
  }

  /**
  * Lấy dữ liệu của tree chỉ tiêu(Modal)
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2019/11/18
  */
  OnchangeNew(value: any) {
    this.mobjNewWarning.mlngServiceId = value;
    if (this.mobjNewWarning.mlngServiceId === -2) {
      if (this.index !== 1) {
        this.vblnCheckService = true;
      }
    } else {
      this.vblnCheckService = false;
    }
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


}
