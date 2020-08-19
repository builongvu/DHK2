import {Component, OnInit, Inject, TemplateRef} from '@angular/core';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE, MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material';
import {TreeviewConfig, TreeviewI18n, TreeviewItem} from 'ngx-treeview';
import {BsModalRef, BsModalService} from 'ngx-bootstrap';
import {ServiceScoreService} from '../../../../../models/serviceScore.service';
import {ServiceService} from '../../../../../services/management/service.service';
import {ToastrService} from 'ngx-toastr';
import {TranslateService} from '@ngx-translate/core';
import {FormControl} from '@angular/forms';
import * as _moment from 'moment';
import {MomentDateAdapter} from '@angular/material-moment-adapter';
import {UnitTreeviewI18n} from '../../../vtt-target/unit-treeview-i18n';

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
  selector: 'app-add-target-weigth',
  templateUrl: './add-target-weigth.component.html',
  styleUrls: ['./add-target-weigth.component.scss'],
  providers: [
    {provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE]},
    {provide: MAT_DATE_FORMATS, useValue: MY_FORMATS_DATE},
    {provide: TreeviewI18n, useClass: UnitTreeviewI18n}
  ],
})
export class AddTargetWeigthComponent implements OnInit {

  mdtDateFromAdd = new FormControl(_moment());
  dateToAdd;
  scoreAdd;
  scoreMaxAdd;
  scoreTemp;
  scoreMaxTemp;
  statusAdd = 1;
  serviceIdAdd;
  servicenameAdd;
  staffAdd = 'null';
  value;
  dataUnit = [];
  staffTreeChose;
  scoreAddFalse = false;
  scoreAddNull = false;
  scoreMaxAddFalse = false;
  scoreMaxAddNull = false;
  dateToAddNull = false;
  dateToAddFalse = false;
  dateFromAddNull = false;
  dateFromAddInvalid = false;
  dateToAddInvalid = false;
  dateFromAddFalse = false;
  errorAdd = false;
  dateNow = new Date().getTime();
  valueAdd = 'VDS';
  staffTrees;
  showAddButton = true;
  nodeTreeViews;
  mobjConfigScore = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 200,
  });
  mobjConfigStaff = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 155,
  });
  shopCode;
  vdsChannelCode;
  mobjModalRef: BsModalRef;
  serviceId;
  staffTree;
  staffs: any[] = [];
  mdtMinDate = new Date(1899, 0, 1);
  mdtMaxDate = new Date(2101, 0, 1);
  maxLengthScore = 4;
  added = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private serviceService: ServiceService,
    private modalService: BsModalService,
    private toastr: ToastrService,
    public dialog: MatDialog,
    private translate: TranslateService,
    private dialogRef: MatDialogRef<AddTargetWeigthComponent>,
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit() {
    this.nodeTreeViews = this.data.treeUnit;
    this.staffTrees = this.data.tree;
    this.staffTreeChose = 'null';
    console.log(this.staffTrees);
    this.dataUnit = this.data.dataUnit;
    this.serviceId = this.data.serviceid;
    // console.log(this.serviceId);
    this.servicenameAdd = this.data.servicenameAdd;
    this.mdtDateFromAdd = new FormControl(_moment());
    this.dateToAdd = new FormControl(null);
    // console.log(this.mdtDateFromAdd);
  }


  unitChange(value: string) {
    this.valueAdd = value;
    this.showAddButton = false;
    this.dataUnit.forEach(
      tree => {
        if (tree.shopCode === value) {
          this.shopCode = tree.shopCode;
          this.vdsChannelCode = tree.vdsChannelCode;
        }
      }
    );
    this.getStaffs();
  }

  getStaffs() {
    this.staffTree = new TreeviewItem({
      text: this.translate.instant('management.weight.choseText'),
      value: 'null',
      children: []
    });
    this.staffs = [];
    this.staffTrees = [];
    // const shopCodes = [];
    try {
      let vdsChannelCodeCst;
      if (this.vdsChannelCode == 'null') {
        vdsChannelCodeCst = '';
      } else {
        vdsChannelCodeCst = this.vdsChannelCode;
      }
      // const shopCodemodel: ShopCodesModel = new ShopCodesModel(this.shopCodes);
      this.serviceService.getStaffs(this.shopCode, vdsChannelCodeCst).subscribe(
        data => {
          this.staffTrees = [];
          // console.log('staff: ', data['data']);
          this.staffs = data['data'];
          this.staffs.forEach(
            staff => {
              const staffTree = new TreeviewItem({
                text: staff['staffName'],
                value: staff['staffCode'],
                children: []
              });
              this.staffTrees.push(staffTree);
            }
          );
          this.staffTrees.unshift(this.staffTree);
          setTimeout(() => {
            this.staffTreeChose = 'null';
          }, 100);
        }
      );
      // console.log('staff: ', this.staffTrees);
    } catch (e) {
      console.log('loi in get Staffs()');
    }

  }

  staffChangeAdd(value: any) {
    this.staffAdd = value;
  }

  changeScoreAdd() {
    this.maxLengthScore = 4;
    if (!this.scoreAdd) {
      this.scoreAddNull = true;
      // this.scoreAddInvalid = false;
      this.scoreAddFalse = false;
      this.showAddButton = true;
    } else if (this.scoreAdd <= 0 || this.scoreAdd > 1 || isNaN(this.scoreAdd) ||
      this.scoreAdd.toString().trim().substring(0, 1) == '.' || this.scoreAdd.toString().trim().substring(0, 2) == '0.') {
      console.log('nho hon 0');
      for (let i = 0; i < this.scoreAdd.length; i++) {
        if (this.scoreAdd[i] == ' ') {
          this.maxLengthScore = this.maxLengthScore + 1;
        }
      }
      this.scoreAddFalse = true;
      this.scoreAddNull = false;
      this.showAddButton = true;
      if (this.scoreAdd.toString().trim().substring(0, 2) == '0.' && this.scoreAdd > 0) {
        console.log('nho hon 0');
        // this.maxLengthScore = this.maxLengthScore;
        this.scoreAddFalse = false;
        this.scoreAddNull = false;
        this.showAddButton = false;
      }
      // this.scoreAddFalse = true;
      // this.scoreAddNull = false;
      // this.showAddButton = true;
    } else {
      this.scoreTemp = this.scoreAdd;
      this.scoreAddFalse = false;
      this.scoreAddNull = false;
      this.showAddButton = false;
    }
    this.scoreTemp = this.scoreAdd;
  }

  changeScoreMaxAdd() {
    this.maxLengthScore = 4;
    if (!this.scoreMaxAdd) {
      this.scoreMaxAddNull = true;
      // this.scoreAddInvalid = false;
      this.scoreMaxAddFalse = false;
      this.showAddButton = true;
    } else if (this.scoreMaxAdd <= 0 || this.scoreMaxAdd > 1 || isNaN(this.scoreMaxAdd) ||
      this.scoreMaxAdd.toString().trim().substring(0, 1) == '.' || this.scoreMaxAdd.toString().trim().substring(0, 2) == '0.') {
      console.log('nho hon 0');
      for (let i = 0; i < this.scoreMaxAdd.length; i++) {
        if (this.scoreMaxAdd[i] == ' ') {
          this.maxLengthScore = this.maxLengthScore + 1;
        }
      }
      this.scoreMaxAddFalse = true;
      this.scoreMaxAddNull = false;
      this.showAddButton = true;
      if (this.scoreMaxAdd.toString().trim().substring(0, 2) == '0.' && this.scoreMaxAdd > 0) {
        console.log('nho hon 0');
        // this.maxLengthScore = this.maxLengthScore;
        this.scoreMaxAddFalse = false;
        this.scoreMaxAddNull = false;
        this.showAddButton = false;
      }
      // this.scoreAddFalse = true;
      // this.scoreAddNull = false;
      // this.showAddButton = true;
    } else {
      this.scoreMaxTemp = this.scoreMaxAdd;
      this.scoreMaxAddFalse = false;
      this.scoreMaxAddNull = false;
      this.showAddButton = false;
    }
    this.scoreMaxTemp = this.scoreMaxAdd;
  }

  noMethod() {

  }

  dateChangeFromAdd(value: any) {

    console.log(value);
    if (!value.value) {
      if (!value.targetElement.value) {
        this.dateFromAddNull = true;
        this.dateFromAddInvalid = false;
        this.dateFromAddFalse = false;
        this.showAddButton = true;
        // console.log('date to add null');
      } else if (value.targetElement.value) {
        this.dateFromAddNull = false;
        this.dateFromAddInvalid = true;
        this.dateFromAddFalse = false;
        this.showAddButton = true;
        // console.log('date to add null');
      }
      return;
    }

    console.log('date from add: ', value.value._d);
    const dateFromAdd = value.value._d.getTime();
    if (dateFromAdd < this.dateNow - 86400000) {
      this.dateFromAddInvalid = false;
      this.dateFromAddNull = false;
      this.dateFromAddFalse = true;
      this.showAddButton = true;
      console.log('date add from ko hop le');
    } else {
      this.dateFromAddInvalid = false;
      this.dateFromAddNull = false;
      this.dateFromAddFalse = false;
      this.mdtDateFromAdd = value;
      this.showAddButton = false;
    }
  }

  dateChangeToAdd(value: any) {
    console.log('date to add: ', value);
    if (!value.value) {
      if (!value.targetElement.value) {
        this.dateToAddInvalid = false;
        this.dateToAdd = new FormControl(null);
        this.dateToAddNull = false;
        this.dateToAddFalse = false;
        this.showAddButton = false;
      } else if (value.targetElement.value) {
        this.dateToAddFalse = false;
        this.dateToAddNull = false;
        this.dateToAddInvalid = true;
        this.showAddButton = true;
        // console.log('date to add null');
      }

      console.error('date to add null');
      return;
    }
    const dateToAdd = value.value._d.getTime();
    // console.log('date from add', this.mdtDateFromAdd);
    const dateFromAdd = this.mdtDateFromAdd.value._d.getTime();
    if (dateToAdd < dateFromAdd - 86400000) {
      this.dateToAddNull = false;
      this.dateToAddInvalid = false;
      this.dateToAddFalse = true;
      this.showAddButton = true;
      console.log('date add to ko hop le');
    } else {
      this.dateToAddNull = false;
      this.dateToAddFalse = false;
      this.dateToAddInvalid = false;
      this.dateToAdd = value;
      this.showAddButton = false;
    }
  }


  setFormAdd() {
    this.dialogRef.close();
  }

  clickSave(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
        ignoreBackdropClick: true
      }
    );
  }


  closeModal2() {
    this.mobjModalRef.hide();
  }

  saveCreate() {
    let staffCodeAdd;
    let vdsChannelCode;
    if (this.vdsChannelCode === 'null') {
      vdsChannelCode = null;
    } else {
      vdsChannelCode = this.vdsChannelCode;
    }
    if (this.staffAdd === 'null') {
      staffCodeAdd = null;
    } else {
      staffCodeAdd = this.staffAdd;
    }

    this.errorAdd = false;
    let serviceModel: ServiceScoreService;
    if (this.scoreAdd && this.mdtDateFromAdd.value && this.scoreMaxAdd) {
      this.scoreAddNull = false;
      this.scoreMaxAddNull = false;
      if (!this.dateToAdd.value) {
        serviceModel = new ServiceScoreService(this.serviceId, this.servicenameAdd, this.statusAdd,
          this.mdtDateFromAdd.value._d.getTime(), null, this.scoreAdd, this.scoreMaxAdd, null, this.shopCode,
          staffCodeAdd, vdsChannelCode);
      } else {
        serviceModel = new ServiceScoreService(this.serviceId, this.servicenameAdd, this.statusAdd,
          this.mdtDateFromAdd.value._d.getTime(), this.dateToAdd.value._d.getTime(), this.scoreAdd, this.scoreMaxAdd, null, this.shopCode,
          staffCodeAdd, vdsChannelCode);
      }
      // console.log('staff code: ', this.staffAdd);
      console.log('form crate: ', serviceModel);
      this.serviceService.createScoreService(serviceModel).subscribe(
        data => {
          console.log('create: ', data);
          if (data['data'] == 'DUPLICATE') {
            console.log(this.scoreTemp);
            this.scoreAdd = this.scoreTemp;
            this.scoreMaxAdd = this.scoreMaxTemp;
            this.toastr.error(this.translate.instant('management.weight.duplicate'),
              this.translate.instant('management.weight.infor'), {
                timeOut: 3000,
                positionClass: 'toast-top-center',
              });
          } else if (data['data'] == 'STAFF_NOT_IN_SHOP') {
            // this.scoreAdd = null;
            // this.scoreTemp = null;
            this.toastr.error(this.translate.instant('management.weight.staffNotUnit'),
              this.translate.instant('management.weight.infor'), {
                timeOut: 3000,
                positionClass: 'toast-top-center',
              });
          } else {
            this.added = true;
            this.serviceService.setReloadWarning(this.added);
            this.scoreAdd = null;
            this.scoreMaxAdd = null;
            this.staffTreeChose = 'null';
            this.statusAdd = 1;
            this.valueAdd = 'VDS';
            this.dateToAdd = new FormControl(null);
            this.mdtDateFromAdd = new FormControl(_moment());
            // this.scoreTemp = null;
            this.mobjModalRef.hide();
            this.toastr.success(this.translate.instant('management.weight.addOk'),
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
          // createModal
          this.serviceIdAdd = this.serviceId;
          // this.scoreAdd = null;
          this.staffAdd = 'null';
          this.mobjModalRef.hide();
          // this.clickSave(model);
        }
      );
    } else {
      this.scoreAddNull = true;
      this.scoreMaxAddNull = true;
      // this.mobjModalRef.hide();
      this.closeModal2();
    }

  }

}
