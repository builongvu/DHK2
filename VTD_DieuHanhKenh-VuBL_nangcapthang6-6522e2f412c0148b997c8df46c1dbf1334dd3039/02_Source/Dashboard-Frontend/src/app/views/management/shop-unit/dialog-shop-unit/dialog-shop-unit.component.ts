import { Component, OnInit, Inject, TemplateRef } from '@angular/core';
import { ShopUnit } from '../../../../models/shopUnit.model';
import { MAT_DIALOG_DATA, MatDialogRef, MatDatepickerModule, DateAdapter, MAT_DATE_LOCALE, MAT_DATE_FORMATS } from '@angular/material';
import { DatePipe } from '@angular/common';
import { MomentDateAdapter } from '@angular/material-moment-adapter';
import { UnitTreeviewI18n } from '../../vtt-target/unit-treeview-i18n';
import { TreeviewI18n } from 'ngx-treeview';
import { MY_FORMATS } from '../../details-report/details-report.component';
import { FormGroup, FormBuilder } from '@angular/forms';
import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { ShopunitService } from '../../../../services/management/shopunit.service';
import { TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr';
import { WarningReceiveService } from '../../../../services/management/warning-receive.service';

@Component({
  selector: 'app-dialog-shop-unit',
  templateUrl: './dialog-shop-unit.component.html',
  styleUrls: ['./dialog-shop-unit.component.scss'],
  providers: [
    MatDatepickerModule,
    DatePipe,

    { provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE] },
    { provide: TreeviewI18n, useClass: UnitTreeviewI18n },
    { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS }
  ],
})
export class DialogShopUnitComponent implements OnInit {

  shopunit: ShopUnit = new ShopUnit();
  marrNodeTreeviewServices;
  TreeShopCode;
  units;
  mdtMinDate = new Date(new Date().setDate(new Date().getDate()));
  mdtMaxDate = new Date(2100, 0, 1);
  mobjConfig = {
    hasFilter: true,
    hasCollapseExpand: true,
    maxHeight: 130,
  };
  channel = '';

  dvalue;
  dvalueUnit;
  type;
  toDateValidToday = false;
  toDateValidPatter = false;
  toDateValidator = false;
  toDateYear = false;
  fromDateValidator = false;
  fromDatePattern = false;
  fromDateNull = false;
  fromDateEndlessToDate = false;
  fromDateYear = false;
  FormSU: FormGroup;
  newShopUnit: ShopUnit = new ShopUnit();
  mblnCheckUnit;
  mblnCheckService;
  mblnCheckShop;
  clickService;
  clickShop;
  channels;
  clickUnit;
  mobjModalRef: BsModalRef;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private dialogRef: MatDialogRef<DialogShopUnitComponent>,
    private datePipe: DatePipe,
    private modalService: BsModalService,
    private shopunitservice: ShopunitService,
    private toastr: ToastrService,
    private translate: TranslateService,
    private warningReceiveSv: WarningReceiveService,
    private fb: FormBuilder
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit() {
    this.marrNodeTreeviewServices = this.data.service;
    this.TreeShopCode = this.data.shop;
    this.units = this.data.unit;
    this.channels = this.data.channels;
    this.dvalue = -1;
    this.dvalueUnit = '-1';
    this.type = this.data.type;
    if (this.type == 1) {
      this.newShopUnit.mstrStatus = '1';
      this.newShopUnit.mstrUnitCode = '-1';
      this.newShopUnit.mlngServiceId = -1;
      this.newShopUnit.mstrShopCode = '-1';
      this.dvalueUnit = '-1';
      this.dvalue = -1;
      this.FormSU = this.fb.group({
        fromDate: [this.mdtMinDate],
        toDate: ['']
      }
      );
    } else {
      this.shopunitservice.getByID(this.data.shopUnit.mlngId).subscribe(res => {
        this.newShopUnit = res.data;
        this.dvalueUnit = this.newShopUnit.mstrShopCode;
        this.dvalue = this.newShopUnit.mlngServiceId;
        if (this.units.filter(item => item.code == this.newShopUnit.mstrUnitCode).length == 0) {
          this.newShopUnit.mstrUnitCode = '-1';
        } else {
          this.mblnCheckUnit = false;
        }
      });
      this.FormSU = this.fb.group({
        fromDate: [this.data.shopUnit.mdtFromDate],
        toDate: [this.data.shopUnit.mdtToDate]
      });
      this.channel = this.data.shopUnit.channel;
      this.fromDateValidator = false;
      this.fromDatePattern = false;
      this.fromDateNull = false;
      this.fromDateEndlessToDate = false;
      this.toDateYear = false;
      this.mblnCheckService = false;
      this.mblnCheckShop = false;
      this.fromDateYear = false;
      this.mdtMinDate = new Date(1900, 0, 1);
    }
  }


  checkFromDateEdit(event) {

    this.fromDateValidator = false;
    this.fromDatePattern = false;
    this.fromDateNull = false;
    this.fromDateEndlessToDate = false;
    this.toDateYear = false;
    this.fromDateYear = false;

    const fromDate = this.datePipe.transform(new Date(this.data.shopUnit.mdtFromDate), 'yyyy-MM-dd');
    const dateChange = this.datePipe.transform(new Date(event.value), 'yyyy-MM-dd');

    if (dateChange == fromDate) {
      this.fromDateValidator = false;
      this.fromDatePattern = false;
      this.fromDateNull = false;
      this.fromDateEndlessToDate = false;
      return;
    } else if (event.targetElement.value.toString().trim().replace(/ /g, '') !== '') {
      const toDateYear = this.datePipe.transform(new Date(event.value), 'yyyy');
      if (Number(toDateYear) >= 2100 || Number(toDateYear) <= 1900) {
        this.fromDateYear = true;
        return;
      } else if (event.value == null) {
        this.fromDatePattern = true;
        return;
      } else {
        const toDay = this.datePipe.transform(new Date(), 'yyyy-MM-dd');
        if (dateChange < toDay) {
          this.fromDateValidator = true;
          return;
        } else if (this.FormSU.get('toDate').value != null && this.FormSU.get('toDate').value != '') {
          const toDate = this.datePipe.transform(new Date(this.FormSU.get('toDate').value), 'yyyy-MM-dd');
          if (dateChange > toDate) {
            this.fromDateEndlessToDate = true;
            return;
          }
        }
      }
    } else if (event.targetElement.value.toString() !== '' && event.value == null) {
      this.fromDatePattern = true;
      return;
    } else {
      this.fromDateNull = true;
      return;
    }
  }

  checkFromDate(event) {
    if (this.type != 1) {
      this.checkFromDateEdit(event);
      return;
    }
    this.fromDateValidator = false;
    this.fromDatePattern = false;
    this.fromDateNull = false;
    this.fromDateEndlessToDate = false;
    this.fromDateYear = false;

    if (event.targetElement.value.toString().trim().replace(/ /g, '') !== '') {
      const fromDateYear = this.datePipe.transform(new Date(event.value), 'yyyy');
      if (Number(fromDateYear) >= 2100 || Number(fromDateYear) <= 1900) {
        this.fromDateYear = true;
        return;
      } else if (event.value == null) {
        this.fromDatePattern = true;
        return;
      } else {
        const toDay = this.datePipe.transform(new Date(), 'yyyy-MM-dd');
        const dateChange = this.datePipe.transform(new Date(event.value), 'yyyy-MM-dd');

        if (dateChange < toDay) {
          this.fromDateValidator = true;
          return;
        } else if (this.FormSU.get('toDate').value != null && this.FormSU.get('toDate').value != '') {
          const toDate = this.datePipe.transform(new Date(this.FormSU.get('toDate').value), 'yyyy-MM-dd');
          if (dateChange > toDate) {
            this.fromDateEndlessToDate = true;
            return;
          }
        } else if (this.FormSU.get('toDate').value == null || this.FormSU.get('toDate').value == '') {
          this.fromDateValidator = false;
          this.fromDatePattern = false;
          this.fromDateNull = false;
          this.fromDateEndlessToDate = false;
          return;
        }
      }
    } else if (event.targetElement.value.toString() !== '' && event.value == null) {
      this.fromDatePattern = true;
      return;
    } else {
      this.fromDateNull = true;
      return;
    }

  }

  onValueChange(value: number) {
    this.newShopUnit.mlngServiceId = value;
    this.checkService();
  }
  onValueChangeUnit(value: string) {
    this.newShopUnit.mstrShopCode = value;
    if (value != '-1') {
      let partner = this.data.marrParther.filter(item => item.shopCode == value)[0];
      if (partner) {
        if (partner.vdsChannelCode == 'null') {
          partner.vdsChannelCode = null;
        }
        this.channel = this.channels.filter(item => item.code == partner.vdsChannelCode)[0].name;
        this.newShopUnit.mstrVdsChannelCode = partner.vdsChannelCode;
      }
    } else {
      this.channel = '';
    }

    this.checkShop();
  }
  clickToService() {
    this.clickService = true;
    this.checkService();
  }
  checkService() {
    if (this.clickService == true) {
      if (this.newShopUnit.mlngServiceId == -1) {
        this.mblnCheckService = true;
      } else {
        this.mblnCheckService = false;
      }
    }
  }

  clickToShop() {
    this.clickShop = true;
    this.checkShop();
  }

  checkShop() {
    if (this.clickShop == true) {
      if (this.newShopUnit.mstrShopCode == '-1') {
        this.mblnCheckShop = true;
      } else {
        this.mblnCheckShop = false;
      }
    }
  }

  checkUnit() {
    if (this.newShopUnit.mstrUnitCode == '-1') {
      this.mblnCheckUnit = true;
    } else {
      this.mblnCheckUnit = false;
    }
  }

  checkToDate(event) {

    this.toDateValidToday = false;
    this.toDateValidPatter = false;
    this.toDateValidator = false;
    this.toDateYear = false;

    const toDay = this.datePipe.transform(new Date(), 'yyyy-MM-dd');
    const dateChange = this.datePipe.transform(new Date(event.value), 'yyyy-MM-dd');

    if (event.targetElement.value.toString().trim().replace(/ /g, '') !== '') {
      const toDateYear = this.datePipe.transform(new Date(event.value), 'yyyy');
      if (Number(toDateYear) >= 2100 || Number(toDateYear) <= 1900) {
        this.toDateYear = true;
        return;
      }
      if (dateChange < toDay) {
        if (dateChange == '1970-01-01') {
          this.toDateValidPatter = true;
          return;
        } else {
          this.toDateValidToday = true;
          return;
        }
      } else if (this.FormSU.get('fromDate').value != null && this.FormSU.get('fromDate').value != '') {
        const fromDate = this.datePipe.transform(new Date(this.FormSU.get('fromDate').value), 'yyyy-MM-dd');
        if (fromDate > dateChange) {
          this.fromDateValidator = false;
          this.fromDatePattern = false;
          this.fromDateNull = false;
          this.fromDateEndlessToDate = true;
          return;
        } else {
          this.fromDateValidator = false;
          this.fromDatePattern = false;
          this.fromDateNull = false;
          this.fromDateEndlessToDate = false;
          return;
        }
      } else {
        if (dateChange == '1970-01-01') {
          this.toDateValidPatter = true;
          return;
        }
      }
    } else if (event.targetElement.value.toString() !== '' && event.value == null) {
      this.toDateValidPatter = true;
      return;
    } else {
      this.fromDateEndlessToDate = false;
      return;
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

    if (this.mblnCheckService === true) {
      this.mobjModalRef.hide();
      return;
    }
    if (this.mblnCheckShop === true) {
      this.mobjModalRef.hide();
      return;
    }
    if (this.mblnCheckUnit === true) {
      this.mobjModalRef.hide();
      return;
    }

    if (this.fromDatePattern === true) {
      this.mobjModalRef.hide();
      this.fromDatePattern = true;
      return;
    }

    if (this.fromDateValidator === true) {
      this.mobjModalRef.hide();
      this.fromDateValidator = true;
      return;
    }

    if (this.fromDateNull === true) {
      this.mobjModalRef.hide();
      this.fromDateNull = true;
      return;
    }

    if (this.fromDateYear === true) {
      this.mobjModalRef.hide();
      this.fromDateYear = true;
      return;
    }

    if (this.fromDateEndlessToDate === true) {
      this.mobjModalRef.hide();
      this.fromDateEndlessToDate = true;
      return;
    }


    if (this.toDateValidator === true) {
      this.mobjModalRef.hide();
      this.toDateValidator = true;
      return;
    }

    if (this.toDateValidPatter === true) {
      this.mobjModalRef.hide();
      this.toDateValidPatter = true;
      return;
    }

    if (this.toDateYear === true) {
      this.mobjModalRef.hide();
      this.toDateYear = true;
      return;
    }

    if (this.toDateValidToday === true) {
      this.mobjModalRef.hide();
      this.toDateValidToday = true;
      return;
    }

    this.newShopUnit.mdtFromDate = this.datePipe.transform(new Date(this.FormSU.get('fromDate').value), 'yyyy-MM-dd');

    if (this.FormSU.get('toDate').value != '' && this.FormSU.get('toDate').value != null) {
      this.newShopUnit.mdtToDate = this.datePipe.transform(
        new Date(this.FormSU.get('toDate').value),
        'yyyy-MM-dd'
      );
    } else {
      this.newShopUnit.mdtToDate = null;
    }
    if (this.newShopUnit.mstrVdsChannelCode == 'null') {
      this.newShopUnit.mstrVdsChannelCode = null;
    }

    this.shopunitservice.save(this.newShopUnit, this.type).subscribe(res => {
      if (res.code == 200) {
        if (this.type == 1) {
          this.showSuccess(this.translate.instant('management.warningconfig.addsuccess'));
          this.fromDateValidator = false;
          this.fromDatePattern = false;
          this.fromDateNull = false;
          this.fromDateEndlessToDate = false;
          this.fromDateYear = false;
          this.fromDateValidator = false;
          this.fromDatePattern = false;
          this.fromDateNull = false;
          this.fromDateEndlessToDate = false;
          this.fromDateYear = false;
          this.dvalue = -1;
          this.dvalueUnit = '-1';
          this.clickService = false;
          this.clickShop = false;
          this.clickUnit = false;
          this.mblnCheckService = null;
          this.mblnCheckShop = null;
          this.mblnCheckUnit = null;
          this.newShopUnit = new ShopUnit();
          this.newShopUnit.mstrStatus = '1';
          this.newShopUnit.mstrUnitCode = '-1';
          this.FormSU = this.fb.group({
            fromDate: [this.mdtMinDate],
            toDate: ['']
          }
          );
          this.warningReceiveSv.setReloadWarning(1);
        } else {
          this.showSuccess(this.translate.instant('management.warningconfig.updatesuccess'));
          this.warningReceiveSv.setReloadWarning(0);
          this.close();
        }
      } else if (res.code == 409) {
        this.showError(this.translate.instant('management.warningconfig.duplicate'));
      } else {
        this.showError(this.translate.instant(res.errors));
      }
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
