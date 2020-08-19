import { Component, OnInit, TemplateRef } from '@angular/core';
import { Inject } from '@angular/core';
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { WarningReceive } from '../../../../models/Warning-Receive';
import { WarningReceiveService } from '../../../../services/management/warning-receive.service';
import { Partner } from '../../../../models/Partner';
import { apParam } from '../../../../models/apParam.model';
import { TreeItem, TreeviewItem, TreeviewConfig } from 'ngx-treeview';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { FormControl } from '@angular/forms';
import * as _moment from 'moment';
import { DashboardModel } from '../../../../models/dashboard.model';
import { BsModalService, BsModalRef } from 'ngx-bootstrap';
import { DialogSendComponent } from '../dialog-send/dialog-send.component';

@Component({
  selector: 'app-dialog-warning-receive',
  templateUrl: './dialog-warning-receive.component.html',
  styleUrls: ['./dialog-warning-receive.component.scss']
})
export class DialogWarningReceiveComponent {
  mstrNewInformlevel: string;
  mstrNewWarninglevel: string;
  mstrNewShopCode: string;
  mobjNewWarningReceive: WarningReceive;
  marrParther: Partner[] = [];
  marrWarninglevels: apParam[] = [];
  marrInformlevels: apParam[] = [];
  marrWarningReceive: WarningReceive[] = [];
  marrNodePartner: TreeviewItem[] = [];
  value: any;
  date = new FormControl(_moment());
  nodeTreeViews: TreeviewItem[] = [];
  vblnCheckWarninglv: boolean;
  vblnCheckInformlv: boolean;
  vblnCheckShopCode: boolean;
  mstrShopcode;
  index = 1;
  dashModel: DashboardModel;
  mobjModalRef: BsModalRef;
  config = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 100,
  });
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private warningReceiveService: WarningReceiveService,
    private toastr: ToastrService,
    public dialog: MatDialog,
    private modalService: BsModalService,
    private dialogRef: MatDialogRef<DialogSendComponent>,
    private translate: TranslateService
  ) {
    dialogRef.disableClose = true;
  }

  /**
 * Lấy dữ liệu truyền vào từ Component và đổ ra modal
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  ngOnInit() {
    this.marrWarninglevels = this.data.warninglv.filter(item => item.status === "1");
    this.marrInformlevels = this.data.informlv.filter(item => item.status === "1");
    this.nodeTreeViews = this.data.shopcode;
    this.mobjNewWarningReceive = new WarningReceive();
    this.mobjNewWarningReceive.mlngId = -1;
    this.warningReceiveService.warningReceives$.subscribe(res => {
      if (this.mobjNewWarningReceive.mlngId === -1) {
        this.nodeTreeViews = this.data.shopcode;
        this.value = "-2";
        this.mobjNewWarningReceive = new WarningReceive();
        this.mstrNewWarninglevel = "-1";
        this.mstrNewInformlevel = "-1";
        this.vblnCheckWarninglv = null;
        this.vblnCheckInformlv = null;
        this.vblnCheckShopCode = null;
        this.index = 1;
      }
    });
    this.mobjNewWarningReceive = this.data.obj;
    if (this.mobjNewWarningReceive.mlngId === -1) {
      this.value = "-2";
      this.mstrNewWarninglevel = "-1";
      this.mstrNewInformlevel = "-1";
      this.index = 1;
    } else {
      this.mobjNewWarningReceive.mstrShopCode = this.mobjNewWarningReceive.mstrShopCode;
      this.mstrNewWarninglevel = "-1";
      this.mstrNewInformlevel = "-1";
      this.mstrShopcode = this.data.shopname;
      for (let i = 0; i < this.marrWarninglevels.length; i++) {
        if (this.marrWarninglevels[i].code === this.mobjNewWarningReceive.mintWarningLevel + "") {
          this.mstrNewWarninglevel = this.mobjNewWarningReceive.mintWarningLevel + "";
        }
      };
      for (let i = 0; i < this.marrInformlevels.length; i++) {
        if (this.marrInformlevels[i].code === this.mobjNewWarningReceive.mintInformLevel + "") {
          this.mstrNewInformlevel = this.mobjNewWarningReceive.mintInformLevel + "";
        }
      };
      this.vblnCheckWarninglv = false;
      this.vblnCheckInformlv = false;
      this.vblnCheckShopCode = false;
    }
  }

  close() {
    this.dialogRef.close();
  }
  dialogReceive(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }

  back() {
    this.mobjModalRef.hide();
  }

  /**
  * Lấy dữ liệu của tree đơn vị được chọn
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2019/11/18
  */
  OnchangeNew(value: any) {
    this.mobjNewWarningReceive.mstrShopCode = value;
    this.checkShopCode();
  }

  checkShopCode() {
    if (this.value === "-2") {
      if (this.index === 1) {
      } else {
        this.vblnCheckShopCode = true;
      }
      this.index = 2;
    } else {
      this.vblnCheckShopCode = false;
    }
  }
  checkWarninglv() {
    if (this.mstrNewWarninglevel === "-1") {
      this.vblnCheckWarninglv = true;
    } else {
      this.vblnCheckWarninglv = false;
    }
  }
  checkInformlv() {
    if (this.mstrNewInformlevel === "-1") {
      this.vblnCheckInformlv = true;
    } else {
      this.vblnCheckInformlv = false;
    }
  }

  /**
  * Thêm mới/cập nhật câu hình cảnh báo
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2019/11/18
  */
  save() {
    this.mobjNewWarningReceive.mintWarningLevel = parseInt(this.mstrNewWarninglevel);
    this.mobjNewWarningReceive.mintInformLevel = parseInt(this.mstrNewInformlevel);
    this.warningReceiveService.SaveWarningReceive(this.mobjNewWarningReceive).subscribe(res => {
      if (res.errors === "Duplicate") {
        this.showError(this.translate.instant('management.warningconfig.duplicate'));
      } else if (res.code === 200) {
        if (this.mobjNewWarningReceive.mlngId == -1) {
          this.showSuccess(this.translate.instant('management.warningconfig.addsuccess'));
          this.warningReceiveService.setReloadWarning(1);
          this.nodeTreeViews = this.data.shopcode;
          this.value = "-2";
          this.mobjNewWarningReceive.mstrShopCode = "-2";
          this.mstrNewWarninglevel = "-1";
          this.mstrNewInformlevel = "-1";
          this.vblnCheckWarninglv = null;
          this.vblnCheckInformlv = null;
          this.vblnCheckShopCode = null;
          this.index = 1;
        } else {
          this.showSuccess(this.translate.instant('management.warningconfig.updatesuccess'));
          this.warningReceiveService.setReloadWarning(0);
          this.dialogRef.close();
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

}
