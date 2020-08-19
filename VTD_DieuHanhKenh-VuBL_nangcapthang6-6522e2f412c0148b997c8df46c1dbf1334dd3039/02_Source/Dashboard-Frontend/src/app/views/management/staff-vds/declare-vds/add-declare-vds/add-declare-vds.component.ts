import {Component, Inject, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE, MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {BsModalRef, BsModalService} from 'ngx-bootstrap';
import {TreeVDSService} from '../../../../../services/management/tree-VDS.service';
import {DeclareVDSModel} from '../../../../../models/declareVDS.model';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from 'ngx-toastr';
import {TreeviewConfig, TreeviewI18n, TreeviewItem} from 'ngx-treeview';
import {MomentDateAdapter} from '@angular/material-moment-adapter';
import {UnitTreeviewI18n} from '../../../vtt-target/unit-treeview-i18n';
import {ServiceService} from '../../../../../services/management/service.service';
import {StaffVdsComponent} from '../../staff-vds.component';

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
  selector: 'app-add-declare-vds',
  templateUrl: './add-declare-vds.component.html',
  styleUrls: ['./add-declare-vds.component.scss'],
  providers: [
    {provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE]},
    {provide: MAT_DATE_FORMATS, useValue: MY_FORMATS_DATE},
    {provide: TreeviewI18n, useClass: UnitTreeviewI18n}
  ],
})
export class AddDeclareVdsComponent implements OnInit {
  @ViewChild(StaffVdsComponent) staffVDS: StaffVdsComponent;

  constructor(private fb: FormBuilder,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private treeVDSService: TreeVDSService,
              private translate: TranslateService,
              private toastr: ToastrService,
              private serviceService: ServiceService,
              private modalService: BsModalService,
              private dialogRef: MatDialogRef<AddDeclareVdsComponent>) {
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
  dataSelect;
  groupVDS;
  dataUnit;
  allUnit: TreeviewItem[] = [];
  isShow = false;
  mobjConfigVDS = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 150,
  });
  shopCode;
  dateFromInvalid = false;
  dateFromNull = false;
  dateToInvalid = false;
  dateThanNow = false;
  dateMoreFrom = false;
  disableBtn = false;
  added = false;
  isClosed = false;

  ngOnInit() {
    const status = this.declareVds.get('status');
    status.setValue(1);
    this.getComboGroup();
    this.declareVds.controls['parentName'].setValue(this.data.dataSelect['shopName']);
    this.declareVds.controls['mdtDateFrom'].setValue(new Date());

    this.getUnitTrees();
    this.getAllUnit();
    if (this.data.shopCode !== 'VDS') {
      this.declareVds.controls['groupVds'].disable();
    } else if (this.data.shopCode === 'VDS') {
      this.declareVds.controls['groupVds'].enable();
    }
  }

  close() {
    this.isClosed = true;
    this.dialogRef.close();
    this.declareVds.reset();
    if (this.added) {
      this.serviceService.setReloadWarning(this.isClosed);
    }

    // this.treeVDSService.reloadTree(this.isClosed);
  }

  createForm() {
    this.declareVds = this.fb.group({
      unitCode: ['', {
        validators: [Validators.required, Validators.maxLength(50),
          Validators.pattern('^[_A-Za-z0-9]*$')], updateOn: 'blur'
      }],
      unitName: [null, {validators: [Validators.required, Validators.maxLength(100)], updateOn: 'blur'}],
      shortName: [null, {validators: [Validators.required, Validators.maxLength(50)], updateOn: 'blur'}],
      parentName: [''],
      groupVds: [''],
      status: {value: 0, disabled: true},
      mdtDateFrom: new FormControl(null),
      mdtDateTo: new FormControl(null),
    });
  }

  onBack() {
    this.mobjModalRef.hide();

  }

  clickSave(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
        ignoreBackdropClick: true
      }
    );
  }

  getUnitTrees() {
    this.treeVDSService.getunitsVDS('').subscribe(
      next => {
        // console.log(next['data']);
        if (!next['data']) {
          return;
        }
        this.dataUnit = next['data'];
      }
    );
  }

  save() {
    let parentShopCode;
    this.dataUnit.forEach(
      data => {
        if (this.declareVds.controls['parentName'].value == data['shopName']) {
          parentShopCode = data['shopCode'];
        }
      }
    );

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

    console.log(this.declareVds.controls['unitCode'].value.trim().length);


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
      parentShopCode,
      this.declareVds.controls['status'].value,
      fromDate, toDate);
    console.log('model create: ', model);
    this.treeVDSService.createVDS(model).subscribe(
      data => {
        console.log('create model: ', model);
        console.log('create data: ', data);
        if (data['data'] == 'DUPLICATE_SHOP_CODE') {
          this.mobjModalRef.hide();
          // this.dialogRef.close();
          // console.log(this.scoreTemp);
          // this.scoreAdd = this.scoreTemp;
          this.toastr.error(this.translate.instant('management.weight.duplicate'),
            this.translate.instant('management.weight.infor'), {
              timeOut: 3000,
              positionClass: 'toast-top-center',
            });
        } else if (data['code'] === 500) {
          this.dialogRef.close();
          this.mobjModalRef.hide();
          this.toastr.error('lỗi server',
            this.translate.instant('management.weight.infor'), {
              timeOut: 3000,
              positionClass: 'toast-top-center',
            });
        } else {
          this.added = true;
          // this.serviceService.setReloadWarning(this.added);
          this.declareVds.controls['unitCode'].reset();
          this.declareVds.controls['unitName'].reset();
          this.declareVds.controls['shortName'].reset();
          // this.declareVds.controls['groupVds'].reset();
          this.declareVds.controls['mdtDateFrom'].setValue(new Date());
          this.declareVds.controls['mdtDateTo'].reset();
          this.mobjModalRef.hide();
          // this.declareVds.reset();
          this.toastr.success(this.translate.instant('management.weight.addOk'),
            this.translate.instant('management.weight.infor'), {
              timeOut: 3000,
              positionClass: 'toast-top-center',
            });
        }
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

  getComboGroup() {
    this.treeVDSService.getGroupVDS(this.data.dataSelect['shopCode']).subscribe(
      data => {
        // console.log(this.data.dataSelect['shopCode']);
        this.groupVDS = data['data'];
        if (data['data'].length === 1) {
          this.declareVds.controls['groupVds'].setValue(data['data'][0]['code']);
        } else {
          this.declareVds.controls['groupVds'].setValue(null);
        }
        // console.log(data['data']);
      }
    );
  }

  getAllUnit() {
    this.treeVDSService.getAllUnit().subscribe(
      data => {
        // console.log('unit: ', data);
        data['data'].forEach(
          unit => {
            this.allUnit.push(new TreeviewItem({
              value: unit['shopCode'],
              text: unit['shopCode'] + ' - ' + unit['shopName']
            }));
          }
        );
        // this.shopCode = this.allUnit[0].value;
        // this.allUnit = data['data'];
      }
    );
  }

  onchange() {
    this.isShow = !this.isShow;
  }

  unitChange(value: any) {
    this.declareVds.controls['unitCode'].setValue(value);
    this.shopCode = value;
    this.allUnit.forEach(
      unit => {
        if (value == unit.value) {
          console.log(unit.text);
          this.declareVds.controls['unitName'].setValue(unit.text.split(' - ')[1]);
        }
      }
    );
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
}
