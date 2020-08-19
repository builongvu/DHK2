import { Component, Inject, OnInit, TemplateRef, OnChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl, ValidatorFn } from '@angular/forms';
import {TreeviewConfig, TreeviewItem} from 'ngx-treeview';
import {ToastrService} from 'ngx-toastr';
import {TranslateService} from '@ngx-translate/core';
import {BsModalRef, BsModalService} from 'ngx-bootstrap';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {TreeVDSService} from '../../../../../services/management/tree-VDS.service';
import {config} from '../../../../../config/application.config';
import {ProductTree} from '../../../../../models/Product.model';
import {AddStaff} from '../../../../../models/AddStaff.model';
import {ShopCodeModel} from '../../../../../models/shopCode.model';

@Component({
  selector: 'app-staff-vds-edit',
  templateUrl: './staff-vds-edit.component.html',
  styleUrls: ['./staff-vds-edit.component.scss']
})
export class StaffVdsEditComponent implements OnInit {
  vdsFormEdit: FormGroup;
  choseText: string;
  fileName: string;
  staffAdd: AddStaff;

  // begin tree
  nodeTreeViews: any;
  valueEdit: any;
  mobjConfigScore = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 150,
  });
  mobjConfigScoreStaff = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 100,
  });
  // end tree
  private mobjModalRef: BsModalRef;
  dataUnit: any[];
  shopCode: any;
  vdsChannelCode: any;
  staffTree: TreeviewItem;
  staffTrees: TreeviewItem[] = [];
  staffTreeChose: any;
  staffChose: any;
  ArrStaffCode: any[];
  isStfNull = true;
  isDirty = false;
  isPreSpace = false;

  constructor(private fb: FormBuilder, private toastr: ToastrService,
              private translate: TranslateService,
              private modalService: BsModalService,
              private treeService: TreeVDSService,
              public dialogReff: MatDialogRef<StaffVdsEditComponent>,
              @Inject(MAT_DIALOG_DATA) public data) {
    this.createForm();
  }

  validation_messages = {
    'staffType': [
      {type: 'required', message: this.translate.instant('management.group.table.validate.notNull')}
    ],
    'staffName': [
      {type: 'required', message: this.translate.instant('management.group.table.validate.notNull')}    ],
    'phoneNumber': [
      {type: 'required', message: this.translate.instant('management.group.table.validate.notNull')},
      {type: 'pattern', message: this.translate.instant('management.group.table.validate.noMatch')}
    ],
    'email': [
      {type: 'required', message: this.translate.instant('management.group.table.validate.notNull')},
      {type: 'email', message: this.translate.instant('management.group.table.validate.noMatch')}
    ]
  };

  ngOnInit() {
    this.nodeTreeViews = this.data.treeUnit;
    this.valueEdit = this.data.valueEdit;
    this.dataUnit = this.data.dataUnit;
    this.getStaffCode();
  }

  // create form
  createForm() {
    this.vdsFormEdit = this.fb.group({
      vdsChannelCode: [this.data.vdsChannelCode],
      staffName: ['', {validators: [Validators.required], updateOn: 'blur'}],
      staffType: [null, {validators: [Validators.required], updateOn: 'blur'}],
      email: ['', {validators: [Validators.required, Validators.email], updateOn: 'blur'}],
      phoneNumber: ['', {
        validators: [Validators.required, Validators.pattern('^[0-9; ]{8,}$')],
        updateOn: 'blur'
      }],
      shopWarning: ['0']
    });
  }
  //  custom validator space
  checkSpace(event) {
    if ((event.target.value || '').trim().length === 0) {
      this.isPreSpace = true;
    } else {
      this.isPreSpace = false;
    }
  }

  // submit form Create
  submitCreate() {
  }

  // colse dialog create
  close() {
    this.dialogReff.close();
  }

  // change combo tree unit
  unitChange(value: any) {
    this.valueEdit = value;
    this.dataUnit.forEach(
      tree => {
        if (tree.shopCode === value) {
          this.shopCode = tree.shopCode;
          this.vdsChannelCode = tree.vdsChannelCode;
          this.vdsFormEdit.controls['vdsChannelCode'].setValue(this.vdsChannelCode);
        }
      }
    );
    this.getStaffCode();
  }

  confirmCreate(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
        ignoreBackdropClick: true
      }
    );
  }
//  staff change event
  staffChange($event: any) {
    this.isStfNull = false;
    this.staffChose = $event;

    this.staffTrees.forEach(
      tree => {
        if (tree.value === $event) {
          this.vdsFormEdit.controls['staffName'].setValue(tree.text);
        }
      }
    );
  }

//  get staff to combox
  getStaffCode() {
    const shopCode: ShopCodeModel = new ShopCodeModel(this.shopCode);
    this.treeService.getStaffCode(shopCode).subscribe(
      next => {
        this.staffTrees = [];
        this.ArrStaffCode = [];
        this.ArrStaffCode = next['data'];
        this.ArrStaffCode.forEach(
          valueUnit => {
            const text = valueUnit['staffCode'];
            const value = valueUnit['staffCode'];
            this.staffTrees.push(new TreeviewItem({text, value, checked: false}));
          }
        );
      },
      error => (this.staffTrees = [], console.log(error))
    );
  }

  //  close confirm dialog
  onBackConfirm() {
    this.mobjModalRef.hide();
  }

  // create staff to unit
  createNewStaff() {
    this.staffAdd = new AddStaff(this.shopCode, this.staffChose, null, null, null, null);
    Object.assign(this.staffAdd, this.vdsFormEdit.value);
    this.treeService.addStafftoUnit(this.staffAdd).subscribe(data => {
      if (data['data'] === 'Success') {
        this.staffTreeChose = null;
        this.showSuccess(this.translate.instant('management.warningconfig.addsuccess'));
        this.vdsFormEdit.reset();
        this.getStaffCode();
        this.vdsFormEdit.patchValue({
          shopWarning: '0',
          vdsChannelCode: this.vdsChannelCode,
        });
      } else if (data['data'] === 'STAFF_IN_VDS_STAFF') {
        this.showError(this.translate.instant('management.warningconfig.duplicate'));
      } else if (data['data'] === 'STAFF_CODE_NOT_EXISTS') {
        this.showError(this.translate.instant('management.warningconfig.notExists'));
      } else if (data['data'] === 'DUPLICATE_EMAIL') {
        this.showError(this.translate.instant('management.declareVDS.dupEmail'));
      } else if (data['data'] === 'PHONE_NUMBER_IS_NOT_VALID') {
        this.showError(this.translate.instant('management.declareVDS.phoneNumberInvalid'));
      } else {
        this.showError(this.translate.instant('management.warningconfig.serverError'));
      }
      this.mobjModalRef.hide();
    });
  }

  // show toaf success after adding a staff
  showSuccess(message: string) {
    this.toastr.success(message,
      this.translate.instant('management.service.message.success'), {
        timeOut: 2000,
        positionClass: 'toast-top-center',
      });
  }

  // show toaf fail after adding a staff
  showError(message: string) {
    this.toastr.error(message,
      this.translate.instant('management.service.message.fail'), {
        timeOut: 2000,
        positionClass: 'toast-top-center',
      });
  }

  changeRadio($event: any) {
  }

  // valid combox staff
  valid() {
    if (!this.staffChose) {
      this.isStfNull = true;
    } else {
      this.isStfNull = false;
    }
  }

  // check dirty combox staff
  dirty() {
    this.isDirty = true;
  }
}

