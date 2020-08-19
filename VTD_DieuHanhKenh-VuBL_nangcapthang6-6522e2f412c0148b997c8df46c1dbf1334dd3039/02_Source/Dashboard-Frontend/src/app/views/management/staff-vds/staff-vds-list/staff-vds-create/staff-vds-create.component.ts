import {Component, Inject, OnInit, TemplateRef} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {BsModalRef, BsModalService} from 'ngx-bootstrap';
import {ToastrService} from 'ngx-toastr';
import {TranslateService} from '@ngx-translate/core';
import {StaffsTable} from '../../../../../models/StaffsTable.model';
import {TreeviewConfig} from 'ngx-treeview';
import {TreeVDSService} from '../../../../../services/management/tree-VDS.service';
import {EditStaffVDSModel} from '../../../../../models/EditStaffVDS.model';

@Component({
  selector: 'app-staff-vds-create',
  templateUrl: './staff-vds-create.component.html',
  styleUrls: ['./staff-vds-create.component.scss']
})
export class StaffVdsCreateComponent implements OnInit {
  vdsFormAdd: FormGroup;
  vdsFormArr;
  updateStaff: EditStaffVDSModel;
  // begin tree
  nodeTreeViews: any;
  valueEdit = 'chọn';
  mobjConfigScore = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 150,
  });
  // end tree
  private mobjModalRef: BsModalRef;
  dataUnit: any[];
  shopCode: any;
  vdsChannelCode: any;
  dirtyChange = false;
  isAllSpace = false;

  constructor(private fb: FormBuilder,
              private toastr: ToastrService,
              private translate: TranslateService,
              private modalService: BsModalService,
              private treeService: TreeVDSService,
              public dialogRef: MatDialogRef<StaffVdsCreateComponent>,
              @Inject(MAT_DIALOG_DATA) public data) {
    dialogRef.disableClose = true;
  }

  validation_messages = {
    'staffType': [
      {type: 'required', message: this.translate.instant('management.group.table.validate.notNull')}
    ],
    'vdsChannelCode': [
      {type: 'required', message: this.translate.instant('management.group.table.validate.notNull')}
    ],
    'staffName': [
      {type: 'required', message: this.translate.instant('management.group.table.validate.notNull')}
    ],
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
    // const{}
    this.nodeTreeViews = this.data.treeUnit;
    this.dataUnit = this.data.dataUnit;
    this.vdsFormArr = this.data['staffsTable'];
    this.valueEdit = this.vdsFormArr.shopCode;
    const a = {'shopCode': this.valueEdit};
    this.createForm();
  }

  createForm() {
    this.vdsFormAdd = this.fb.group({
      id: [this.vdsFormArr.id],
      vdsChannelCode: [this.vdsFormArr.vdsChannelCode],
      staffCode: [this.vdsFormArr.staffCode, [Validators.required]],
      staffName: [this.vdsFormArr.staffName, {validators: [Validators.required], updateOn: 'blur'}],
      staffType: [this.vdsFormArr.staffType, [Validators.required]],
      email: [this.vdsFormArr.email, {validators: [Validators.required, Validators.email], updateOn: 'blur'}],
      phoneNumber: [this.vdsFormArr.phoneNumber, {
        validators: [Validators.required, Validators.pattern('^[0-9; ]{8,}$')],
        updateOn: 'blur'
      }],
      shopWarning: [this.vdsFormArr.shopWarning, [Validators.required]]
    });
  }
  //  checkSpace
  checkSpace(event) {
    if ((event.target.value || '').trim().length === 0) {
      this.isAllSpace = true;
    } else {
      this.isAllSpace = false;
    }
  }

  // get value form
  submit() {
    const {channel, codeNv, nameNv, phone, depart, position} = this.vdsFormAdd.value;
  }

  // close dialog add staff
  close() {
    this.dialogRef.close();
  }

  // open dialog confirm
  confirmCreate(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
        ignoreBackdropClick: true
      }
    );
  }

  // close dialog
  onBackConfirm() {
    this.mobjModalRef.hide();
  }

  // method add staff
  updateStaffVDS() {
    const formVl = this.vdsFormAdd.value;
    this.updateStaff = new EditStaffVDSModel(null, null, null, null, null, this.valueEdit);
    Object.assign(this.updateStaff, formVl);
    this.treeService.updateStaff(this.updateStaff).subscribe(data => {
      if (data['data'] === 'Success') {
        this.showSuccess(this.translate.instant('management.warningconfig.updatesuccess'));
        this.dialogRef.close();
      } else if( data['data'] === 'Duplicate') {
        this.showError(this.translate.instant('management.warningconfig.duplicate'));
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

  unitChange(value: any) {
    this.valueEdit = value;
    this.dataUnit.forEach(
      tree => {
        if (tree.shopCode === value) {
          this.shopCode = tree.shopCode;
          this.vdsChannelCode = tree.vdsChannelCode;
          this.vdsFormAdd.controls['vdsChannelCode'].setValue(this.vdsChannelCode);
        }
      }
    );
    // console.log('shopCode: edit ', this.shopCode);
    // console.log('vdsChannelCode: edit ', this.vdsChannelCode);
  }

  dirty() {
    this.dirtyChange = true;
  }
}
