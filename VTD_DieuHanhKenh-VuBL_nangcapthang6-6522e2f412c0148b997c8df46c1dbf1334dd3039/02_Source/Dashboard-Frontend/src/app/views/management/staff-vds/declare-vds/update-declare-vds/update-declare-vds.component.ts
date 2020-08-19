import {Component, Inject, OnInit, TemplateRef} from '@angular/core';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE, MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {BsModalRef, BsModalService} from 'ngx-bootstrap';
import {TreeVDSService} from '../../../../../services/management/tree-VDS.service';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from 'ngx-toastr';
import {DeclareVDSModel} from '../../../../../models/declareVDS.model';
import {MomentDateAdapter} from '@angular/material-moment-adapter';
import {TreeItem, TreeviewConfig, TreeviewI18n, TreeviewItem} from 'ngx-treeview';
import {UnitTreeviewI18n} from '../../../vtt-target/unit-treeview-i18n';
import {UnitModel} from '../../../../../models/unit.model';
import {ServiceService} from '../../../../../services/management/service.service';

export const MY_FORMATS_DATE = {
  parse: {
    dateInput: 'DD/MM/YYYY',
  },
  display: {
    dateInput: 'DD/MM/YYYY',
    monthYearLabel: 'DDD MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'DDD MMMM YYYY',
  },
};

@Component({
  selector: 'app-update-declare-vds',
  templateUrl: './update-declare-vds.component.html',
  styleUrls: ['./update-declare-vds.component.scss'],
  providers: [
    {provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE]},
    {provide: MAT_DATE_FORMATS, useValue: MY_FORMATS_DATE},
    {provide: TreeviewI18n, useClass: UnitTreeviewI18n}
  ],
})
export class UpdateDeclareVdsComponent implements OnInit {

  constructor(private fb: FormBuilder,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private treeVDSService: TreeVDSService,
              private modalService: BsModalService,
              private serviceService: ServiceService,
              private translate: TranslateService,
              private toastr: ToastrService,
              private dialogRef: MatDialogRef<UpdateDeclareVdsComponent>) {
    this.createForm();
    dialogRef.disableClose = true;
  }

  validation_messages = {
    'unitCode': [
      {type: 'required', message: this.translate.instant('management.declareVDS.dataNull')},
      {type: 'maxlength', message: this.translate.instant('management.declareVDS.data50')},
      {type: 'pattern', message: this.translate.instant('management.declareVDS.dataInvalid')}
    ],
    'unitName': [
      {type: 'required', message: this.translate.instant('management.declareVDS.dataNull')},
      {type: 'maxlength', message: this.translate.instant('management.declareVDS.data100')}
    ],
    'shortName': [
      {type: 'required', message: this.translate.instant('management.declareVDS.dataNull')},
      {type: 'maxlength', message: this.translate.instant('management.declareVDS.data50')}
    ],
    'groupVds': [
      {type: 'required', message: this.translate.instant('management.declareVDS.dataNull')}
    ],
    'status': [
      {type: 'required', message: this.translate.instant('management.declareVDS.dataNull')}
    ]
  };
  declareVds: FormGroup;
  private mobjModalRef: BsModalRef;
  groupVDS;
  dateFromInvalid = false;
  dateFromNull = false;
  dateToInvalid = false;
  dateThanNow = false;
  dateMoreFrom = false;
  disableBtn = false;

  valueUnit;

  mobjConfigVDS = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 80,
  });
  valueVds;
  nodeTreeViews;

  ngOnInit() {
    console.log('data updated: ', this.data);
    this.nodeTreeViews = this.data.tree;
    this.valueVds = this.data.data['parentShopCode'];
    console.log('this.value', this.valueVds);
    this.getComboGroup(this.valueVds);
    if (this.data.data['fromDate']) {
      this.declareVds.controls['mdtDateFrom'].setValue(new Date(this.data.data['fromDate']));
    } else {
      this.declareVds.controls['mdtDateFrom'].setValue(null);
    }

    if (this.data.data['toDate']) {
      // console.log('to date', table['toDate']);
      // this.declareVds.controls['mdtDateTo'].setValue(new Date());
      this.declareVds.controls['mdtDateTo'].setValue(new Date(this.data.data['toDate']));
    } else {
      this.declareVds.controls['mdtDateTo'].setValue(null);
    }

    if (this.valueVds !== 'VDS') {
      this.declareVds.controls['groupVds'].disable();
    } else if (this.valueVds === 'VDS') {
      this.declareVds.controls['groupVds'].enable();
    }
  }

  close() {
    this.dialogRef.close();
  }

  submit() {
    // const {channel, codeNv, nameNv, phone, depart, position} = this.vdsForm.value;
    // console.log(`channel: ${channel}, codeNV: ${codeNv} nameNv: ${nameNv}, phone: ${phone}, depart: ${depart}, position: ${position}`);
  }

  createForm() {
    this.declareVds = this.fb.group({
      unitCode: [{value: this.data.data['shopCode'], disabled: true},
        {validators: [Validators.required, Validators.maxLength(50), Validators.pattern('^[_A-Za-z0-9]*$')], updateOn: 'blur'}],
      unitName: [this.data.data['shopName'], {validators: [Validators.required, Validators.maxLength(100)], updateOn: 'blur'}],
      shortName: [this.data.data['shortName'], {validators: [Validators.required, Validators.maxLength(50)], updateOn: 'blur'}],
      groupVds: [{value: this.data.data['vdsChannelCode'], disabled: true}],
      status: [this.data.data['status']],
      mdtDateFrom: new FormControl(null),
      mdtDateTo: new FormControl(null),
    });
  }

  onBack() {
    this.mobjModalRef.hide();
  }

  getComboGroup(shopCode) {
    this.treeVDSService.getGroupVDS(shopCode).subscribe(
      data => {
        this.groupVDS = data['data'];
        if (data['data'].length === 1) {
          this.declareVds.controls['groupVds'].setValue(data['data'][0]['code']);
        }
        console.log(data['data']);
      }
    );
  }

  dateChangeFrom(value: any) {
    console.log('date fromd: ', value);
    if (!value.value) {
      if (!value.targetElement.value) {
        this.dateFromNull = true;
        this.dateFromInvalid = false;
        this.dateThanNow = false;
        this.disableBtn = true;
      } else if (value.targetElement.value) {
        this.dateFromNull = false;
        this.dateFromInvalid = true;
        this.dateThanNow = false;
        this.disableBtn = true;
      }
      return;
    }

    this.declareVds.controls['mdtDateFrom'].setValue(value.value._d);
    const dateNow = new Date().getTime();
    // console.log(dateNow);
    // console.log(new Date(value.value));
    if (new Date(value.value).getTime() < dateNow - 86400000) {
      this.dateFromNull = false;
      this.dateFromInvalid = false;
      this.dateThanNow = true;
      this.disableBtn = true;
    } else {
      this.dateFromNull = false;
      this.dateFromInvalid = false;
      this.dateThanNow = false;
      this.disableBtn = false;

      if (this.declareVds.controls['mdtDateTo'].value) {
        const dateTo = this.declareVds.controls['mdtDateTo'].value;
        const fromDate = value.value._d.getTime();
        // console.log(dateTo.getTime(), fromDate);
        if (dateTo.getTime() < fromDate) {
          this.dateMoreFrom = true;
          this.dateToInvalid = false;
          this.disableBtn = true;
        } else {
          this.dateMoreFrom = false;
          this.dateToInvalid = false;
          this.disableBtn = false;
        }
      }
    }
  }

  dateChangeTo(value: any) {

    // console.log(value.value._d.getTime());
    if (!value.targetElement.value) {
      this.dateToInvalid = false;
      this.disableBtn = false;
      this.dateMoreFrom = false;
    } else if (value.value) {
      // this.dateToInvalid = false;
      // this.disableBtn = false;
      // this.dateMoreFrom = false;

      const fromDate = this.declareVds.controls['mdtDateFrom'].value.getTime();
      if (value.value._d.getTime() < fromDate - 86400000 + 100) {
        this.dateMoreFrom = true;
        this.dateToInvalid = false;
        this.disableBtn = true;
      } else {
        this.declareVds.controls['mdtDateTo'].setValue(value.value._d);
        this.dateMoreFrom = false;
        this.dateToInvalid = false;
        this.disableBtn = false;
      }
    } else {
      this.dateToInvalid = true;
      this.dateMoreFrom = false;
      this.disableBtn = true;
    }
    // console.log('date to: ', (this.declareVds.controls['unitCode'].invalid));
    // console.log('date to: ', (this.declareVds.controls['unitCode'].valid));
  }

  clickSave(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
        ignoreBackdropClick: true
      }
    );
  }

  updateUnit() {

    let fromDate;
    let toDate;
    if (!this.declareVds.controls['mdtDateFrom'].value) {
      fromDate = null;
    } else {
      fromDate = this.declareVds.controls['mdtDateFrom'].value.getTime();
    }

    if (!this.declareVds.controls['mdtDateTo'].value) {
      toDate = null;
    } else {
      toDate = this.declareVds.controls['mdtDateTo'].value.getTime();
    }

    let vdsChannelCode;
    if (this.declareVds.controls['groupVds'].value == '') {
      vdsChannelCode = null;
    } else {
      vdsChannelCode = this.declareVds.controls['groupVds'].value;
    }

    const model: DeclareVDSModel = new DeclareVDSModel(
      vdsChannelCode,
      this.declareVds.controls['unitCode'].value,
      this.declareVds.controls['unitName'].value,
      this.declareVds.controls['shortName'].value,
      this.valueVds,
      this.declareVds.controls['status'].value,
      fromDate, toDate, null, this.data.data['id']);
    this.treeVDSService.updateDeclareVDS(model).subscribe(
      data => {
        console.log('update model: ', model);
        console.log('edit: ', data);
        if (data['data'] == 'DUPLICATE') {
          this.toastr.error(this.translate.instant('management.weight.duplicate'),
            this.translate.instant('management.weight.infor'), {
              timeOut: 3000,
              positionClass: 'toast-top-center',
            });
        } else if (data['data'] == 'INVALID_PARENT_SHOP') {
          this.toastr.error(this.translate.instant('management.declareVDS.parNotChild'),
            this.translate.instant('management.weight.infor'), {
              timeOut: 3000,
              positionClass: 'toast-top-center',
            });
        } else {
          this.mobjModalRef.hide();
          this.dialogRef.close();
          this.serviceService.setReloadWarning(true);
          // this.setFormAdd();
          // this.loadData();
          this.toastr.success(this.translate.instant('management.weight.editOk'),
            this.translate.instant('management.weight.infor'), {
              timeOut: 3000,
              positionClass: 'toast-top-center',
            });
        }
      },
      error => {
        console.log(error);
      },
      () => {
        this.mobjModalRef.hide();
      }
    );
    this.mobjModalRef.hide();
  }

  checkNull() {
    if (!this.declareVds.controls['unitCode'].value || this.declareVds.controls['unitCode'].value.trim().length === 0) {
      this.declareVds.controls['unitCode'].setValue(null);
    } else {
      this.declareVds.controls['unitCode'].setValue(this.declareVds.controls['unitCode'].value.trim());
    }

    if (!this.declareVds.controls['unitName'].value || this.declareVds.controls['unitName'].value.trim().length === 0) {
      this.declareVds.controls['unitName'].setValue(null);
    } else {
      this.declareVds.controls['unitName'].setValue(this.declareVds.controls['unitName'].value.trim());
    }

    if (!this.declareVds.controls['shortName'].value || this.declareVds.controls['shortName'].value.trim().length === 0) {
      this.declareVds.controls['shortName'].setValue(null);
    } else {
      this.declareVds.controls['shortName'].setValue(this.declareVds.controls['shortName'].value.trim());
    }
  }

  unitChange(value: string) {
    this.valueVds = value;
    this.getComboGroup(value);
  }
}
