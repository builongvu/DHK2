import { Component, OnInit, TemplateRef } from '@angular/core';
import { DVT, DVTDTO } from '../../../../models/dvt.model';
import { UnitService } from '../../../../services/management/unit.service';
import { Pager } from '../../../../models/Pager';
import { DialogUnitComponent } from '../dialog-unit/dialog-unit.component';
import { MatDialog } from '@angular/material';
import { WarningReceiveService } from '../../../../services/management/warning-receive.service';
import { TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr';
import { BsModalService, BsModalRef } from 'ngx-bootstrap';

@Component({
  selector: 'app-unit-dvt',
  templateUrl: './unit.component.html',
  styleUrls: ['./unit.component.scss']
})
export class UnitComponent implements OnInit {

  units: DVT[] = [];
  mblnCheckAll = false;
  mstrunlock = '0';
  mstrlock = '0';
  mobjModalRef: BsModalRef;
  rate: string = '';
  unitDTO: DVTDTO = new DVTDTO;
  mblnConfirm;
  constructor(
    public dialog: MatDialog,
    private warningReceiveService: WarningReceiveService,
    private modalService: BsModalService,
    private toastr: ToastrService,
    private translate: TranslateService,
    private unitService: UnitService
  ) { }

  ngOnInit() {
    this.unitDTO.status = -1;
    this.unitDTO.pager = new Pager(1, 10);
    this.getUnits();
    this.warningReceiveService.reloadWarning$.subscribe(page => {
      if (page == 1) {
        this.unitDTO.pager.page = 1;
      }
      this.getUnits();
    });
  }

  /**
  * Sự kiện khi click checkbox 
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  select(id: number) {
    this.mstrunlock = '0';
    this.mstrlock = '0';
    this.units.forEach(unit => {
      if (unit.id == id) {
        unit.check = !unit.check;
      }
      if (unit.check) {
        if (unit.status == 0) {
          this.mstrunlock = '1';
        } else {
          this.mstrlock = '1';
        }
      }
    });
    let varrCheck = this.units.filter(item => item.check == true);
    if (varrCheck.length == this.units.length) {
      this.mblnCheckAll = true;
    } else {
      this.mblnCheckAll = false;
    }
  }

  /**
  * Khóa, mở khóa bản ghi
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  lockUnlock(status: string) {
    let marr = this.units.filter(item => item.check);
    let id = [];
    marr.forEach((unit: DVT) =>{
      id.push(unit.id.toString());
    });
    this.unitService.lockUnlock(id, status).subscribe( res =>{
      if (res.code === 200) {
        this.showSuccess(res.data);
      } else {
        this.showError(res.errors);
      };
      this.mstrunlock = '0';
      this.mstrlock = '0';
      this.getUnits();
      this.back();
    })
  }


  showSuccess(message: string) {
    this.toastr.success(message,
      this.translate.instant('management.warningconfig.success'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }


  /**
  * Thông báo thất bại
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
  openConfirm(pobjTemplate: TemplateRef<any>, status: string) {
    if (status == '1') {
      this.mblnConfirm = true;
    } else {
      this.mblnConfirm = false;
    }
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
  * Sự kiện khi chọn checkbox tất cả
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  checkAll() {
    this.mstrunlock = '0';
    this.mstrlock = '0';
    this.units.forEach(unit => {
      unit.check = !this.mblnCheckAll;
      if (unit.check) {
        if (unit.status == 0) {
          this.mstrunlock = '1';
        } else {
          this.mstrlock = '1';
        }
      }
    });
  }

  /**
  * Sự kiện khi chuyển trang
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  pageChange(p: any) {
    this.unitDTO.pager.page = p;
    this.getUnits();
  }
  
  /**
  * Mở modal thêm mới
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  openNew() {
    const vdialog = this.dialog.open(DialogUnitComponent, {
      data: {
        type: 1
      }
    });
    vdialog.afterClosed().subscribe(result => {

    });
  }

  
  /**
  * Mở modal sửa
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  openEdit(unit: DVT) {
    const vdialog = this.dialog.open(DialogUnitComponent, {
      data: {
        type: 0,
        unit: unit
      }
    });
    vdialog.afterClosed().subscribe(result => {

    });
  }

  
  /**
  * Lấy tất cả đơn vị
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  getUnits() {
    let unit = new DVTDTO();
    if (this.unitDTO.code && this.unitDTO.code.trim().length > 0) {
      unit.code = this.unitDTO.code.trim();
    }
    if (this.unitDTO.name && this.unitDTO.name.trim().length > 0) {
      unit.name = this.unitDTO.name.trim();
    }
    if (this.rate && this.rate.trim().length > 0) {
      unit.rate = Number(this.rate.trim());
    }
    if(this.unitDTO.status != -1){
      unit.status = this.unitDTO.status;
    }
    unit.pager = this.unitDTO.pager;
    this.unitService.getUnitByCondition(unit).subscribe((res: any) => {
      this.units = res.data;
      this.unitDTO.pager.totalRow = res.totalRow;
      this.mblnConfirm = false;
      this.mblnCheckAll = false;
    })
  }

}
