import { Component, OnInit, Inject, TemplateRef, AfterViewChecked } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { DVT } from '../../../../models/dvt.model';
import { BsModalService, BsModalRef } from 'ngx-bootstrap';
import { UnitService } from '../../../../services/management/unit.service';
import { WarningReceiveService } from '../../../../services/management/warning-receive.service';

@Component({
  selector: 'app-dialog-unit',
  templateUrl: './dialog-unit.component.html',
  styleUrls: ['./dialog-unit.component.scss']
})
export class DialogUnitComponent implements OnInit, AfterViewChecked {
  ngAfterViewChecked(): void {
    // throw new Error("Method not implemented.");
  }

  formUnit: FormGroup;
  newUnit: DVT = new DVT();
  type;
  units: DVT[] = [];
  checkname: boolean;
  checkcode: boolean;
  mobjModalRef: BsModalRef;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private dialogRef: MatDialogRef<DialogUnitComponent>,
    private toastr: ToastrService,
    private translate: TranslateService,
    private modalService: BsModalService,
    private fb: FormBuilder,
    private warningReceiveSv: WarningReceiveService,
    private unitService: UnitService
  ) { dialogRef.disableClose = true; }

  ngOnInit() {
    this.createRoleFrom();
    this.type = this.data.type;
    if (this.type == 1) {
      this.newUnit.status = 1;
      this.createRoleFrom();
    } else {
      this.unitService.getByID(this.data.unit.id).subscribe((res: any) => {
        this.newUnit = res.data;
        this.formUnit = this.fb.group({
          code: [{ value: this.newUnit.code, disabled: true }],
          name: [this.newUnit.name, [Validators.required]],
          rate: [this.newUnit.rate, [Validators.required, Validators.pattern('^\\s{0,20}?([0-9]{1,20})\\s{0,20}$')]],
          status: [this.newUnit.status, [Validators.required]]
        });
      });
    }
    this.getUnits();
  }

  /**
  * Lấy tất cả đơn vị
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  getUnits() {
    this.unitService.getUnit().subscribe((res: any) => {
      this.units = res.data;
    })
  }

  /**
  * Kiểm tra mã đơn vị
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  checkCode() {
    if (this.newUnit.code && this.newUnit.code.trim().length > 0) {
      this.newUnit.code = this.newUnit.code.toUpperCase();
      if (this.units.filter(item => item.code.toUpperCase().trim() == this.newUnit.code && item.id != this.newUnit.id).length > 0) {
        this.checkcode = true;
      } else {
        this.checkcode = false;
      }
    }
  }

  /**
  * Lưu thay đổi
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  save() {
    if (!this.formUnit.valid || this.checkname != false || this.checkcode != false) {
      return;
    }

    this.newUnit.code = this.newUnit.code.toUpperCase().trim();
    this.newUnit.name = this.newUnit.name.trim();

    this.unitService.saveUnit(this.newUnit, this.type).subscribe(res => {
      if (res.code == 200) {
        if (this.type == 1) {
          this.showSuccess(this.translate.instant('management.warningconfig.addsuccess'));
          this.newUnit = new DVT();
          this.newUnit.status = 1;
          this.createRoleFrom();
          this.checkname = null;
          this.checkcode = null;
          this.getUnits();
          this.warningReceiveSv.setReloadWarning(1);
        } else {
          this.showSuccess(this.translate.instant('management.warningconfig.updatesuccess'));
          this.warningReceiveSv.setReloadWarning(0);
          this.close();
        }
      } else {
        this.showError(res.errors);
      }
      this.back();
    });

  }

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

  /**
  * Mở popup xác nhận
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  openComfirm(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }

  /**
  * Đóng popup xác nhận
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  back() {
    this.mobjModalRef.hide();
  }

  /**
  * Tạo form
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  createRoleFrom() {
    this.formUnit = this.fb.group({
      code: ['', [Validators.required, Validators.pattern('^\\s{0,40}?[_A-Za-z0-9]{1,50}\\s{0,40}$')]],
      name: ['', [Validators.required]],
      rate: ['', [Validators.required, Validators.pattern('^\\s{0,15}?([0-9]{1,20}(\\.([0-9]{1,2}))?)\\s{0,15}$')]],
      status: [1, [Validators.required]]
    });
  }

  /**
  * Kiểm tra tên đơn vị
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  checkName() {
    if (this.newUnit.name) {
      if (this.newUnit.name.trim().length == 0) {
        this.checkname = true;
      } else {
        this.checkname = false;
      }
    } else {
      this.checkname = false;
    }
  }

  hasError(controlName: string, errorName: string) {
    if (this.formUnit) {
      return this.formUnit.controls[controlName].hasError(errorName);
    }
  }

  close() {
    this.dialogRef.close();
  }

}
