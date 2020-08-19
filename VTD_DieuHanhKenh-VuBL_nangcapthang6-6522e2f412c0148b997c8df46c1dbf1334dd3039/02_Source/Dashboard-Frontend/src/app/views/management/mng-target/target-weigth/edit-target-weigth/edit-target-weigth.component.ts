import { Component, OnInit, Inject, TemplateRef } from '@angular/core';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE, MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material';
import { ServiceService } from '../../../../../services/management/service.service';
import { BsModalService, BsModalRef } from 'ngx-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr';
import {TreeviewConfig, TreeviewI18n, TreeviewItem} from 'ngx-treeview';
import { ServiceScoreService } from '../../../../../models/serviceScore.service';
import {MomentDateAdapter} from '@angular/material-moment-adapter';
import {UnitTreeviewI18n} from '../../../vtt-target/unit-treeview-i18n';
import {FormControl} from '@angular/forms';

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
  selector: 'app-edit-target-weigth',
  templateUrl: './edit-target-weigth.component.html',
  styleUrls: ['./edit-target-weigth.component.scss'],
  providers: [
    { provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE] },
    { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS_DATE },
    { provide: TreeviewI18n, useClass: UnitTreeviewI18n }
  ],
})
export class EditTargetWeigthComponent implements OnInit {

  mdtDateFromEdit;
  mdtDateToEdit: FormControl = new FormControl();
  scoreEdit;
  scoreMaxEdit;
  statusEdit;
  idRowEdit;
  serviceIdEdit;
  servicenameEdit;
  staffEdit = 'null';
  scoreEditFalse = false;
  scoreMaxEditFalse = false;
  scoreEditNull = false;
  scoreMaxEditNull = false;
  dateToEditNull = false;
  dateToEditFalse = false;
  dateFromEditNull = false;
  dateFromEditInvalid = false;
  dateToEditInvalid = false;
  dateFromEditFalse = false;
  errorEdit = false;
  valueEdit;
  dataEdit;
  nodeTreeViews;
  staffTrees;
  staffTreeChose;
  showEditButton;
  shopCode;
  dateNow = new Date().getTime();
  vdsChannelCode;
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
  value;
  dataUnit;
  mobjModalRef: BsModalRef;
  staffTree;
  choseText = this.translate.instant('management.weight.choseText');
  staffs: any[] = [];
  mdtMinDate = new Date(1899, 0, 1);
  mdtMaxDate = new Date(2101, 0, 1);
  maxLengthScore = 4;
  staffEditName;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private serviceService: ServiceService,
    private modalService: BsModalService,
    private toastr: ToastrService,
    public dialog: MatDialog,
    private translate: TranslateService,
    private dialogRef: MatDialogRef<EditTargetWeigthComponent>,
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit() {
    this.nodeTreeViews = this.data.treeUnit;
    this.staffTrees = this.data.tree;
    this.valueEdit = this.data.valueEdit;
    this.showEditButton = false;
    this.staffEditName = this.data.staffEditName;
    this.dataUnit = this.data.dataUnit;
    this.serviceIdEdit = this.data.serviceIdEdit;
    this.idRowEdit = this.data.idRowEdit;
    this.scoreEdit = this.data.scoreEdit;
    this.scoreMaxEdit = this.data.scoreMaxEdit;
    this.servicenameEdit = this.data.servicenameEdit;
    this.statusEdit = this.data.statusEdit;
    this.mdtDateFromEdit = this.data.mdtDateFromEdit;
    this.mdtDateToEdit = this.data.mdtDateToEdit;
    // console.log('DateToEdit', this.mdtDateToEdit.value._d);
    // console.log('DateFromEdit', this.mdtDateFromEdit.value._d);
    // this.getStaffs();
  }

  unitChange(value: string) {
    this.valueEdit = value;
    this.dataUnit.forEach(
      tree => {
        if (tree.shopCode === value) {
          this.shopCode = tree.shopCode;
          this.vdsChannelCode = tree.vdsChannelCode;
        }
      }
    );
    // console.log('shopCode: ', this.shopCode);
    // console.log('vdsChannelCode: ', this.vdsChannelCode);
    this.getStaffs();
  }

  setFormAdd() {
    this.dialogRef.close();
  }
  getStaffs() {
    this.staffTree = new TreeviewItem({
      text: this.choseText,
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
          this.staffs.forEach(
            staff => {
              if (this.staffEditName == staff['staffName']) {
                this.staffEdit = staff['staffCode'];
                console.log('code: ', this.staffEdit);
              }
            }
          );
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


  dateChangeToEdit(value: any) {
    if (!value.value) {
      if (!value.targetElement.value) {
        this.dateToEditInvalid = false;
        this.mdtDateToEdit = new FormControl(null);
        this.dateToEditNull = false;
        this.dateToEditFalse = false;
        this.showEditButton = false;
      } else if (value.targetElement.value) {
        console.log('ngoc');
        this.dateToEditFalse = false;
        this.dateToEditNull = false;
        this.dateToEditInvalid = true;
        this.showEditButton = true;
        // console.log('date to add null');
      }

      console.error('date to add null');
      return;
    }
    const dateToEdit = value.value._d.getTime();
    // console.log('date from add', this.mdtDateFromAdd);
    const dateFromEdit = this.mdtDateFromEdit.value._d.getTime();
    if (dateToEdit < dateFromEdit) {
      this.dateToEditNull = false;
      this.dateToEditFalse = true;
      this.dateToEditInvalid = false;
      this.showEditButton = true;
      console.log('date add to ko hop le');
    } else {
      this.dateToEditNull = false;
      this.dateToEditFalse = false;
      this.mdtDateToEdit = value;
      this.dateToEditInvalid = false;
      this.showEditButton = false;
    }
  }

  changeScoreEdit() {
    // if (this.scoreEdit == 0) {
    //   this.scoreEditFalse = true;
    //   this.scoreEditNull = false;
    //   this.showEditButton = true;
    // }
    this.maxLengthScore = 4;
    // console.log(typeof this.scoreEdit, this.scoreEdit.toString().substring(0,1));
    if (!this.scoreEdit) {
      this.scoreEditNull = true;
      this.scoreEditFalse = false;
      this.showEditButton = true;
    } else if (this.scoreEdit <= 0 || this.scoreEdit > 1 || isNaN(this.scoreEdit) ||
      this.scoreEdit.toString().trim().substring(0, 1) == '.' || this.scoreEdit.toString().trim().substring(0, 2) == '0.') {
      console.log('ngoc');
      for (let i = 0; i < this.scoreEdit.length; i++) {
        if (this.scoreEdit[i] == ' ') {
          this.maxLengthScore = this.maxLengthScore + 1;
        }
      }
      this.scoreEditFalse = true;
      this.scoreEditNull = false;
      this.showEditButton = true;
      if (this.scoreEdit.toString().trim().substring(0, 2) == '0.' && this.scoreEdit > 0) {
        // this.maxLengthScore = this.maxLengthScore;
        this.scoreEditFalse = false;
        this.scoreEditNull = false;
        this.showEditButton = false;
      }
    } else {
      this.scoreEditFalse = false;
      this.scoreEditNull = false;
      this.showEditButton = false;
    }
  }

  changeScoreMaxEdit() {
    // if (this.scoreEdit == 0) {
    //   this.scoreEditFalse = true;
    //   this.scoreEditNull = false;
    //   this.showEditButton = true;
    // }
    this.maxLengthScore = 4;
    // console.log(typeof this.scoreEdit, this.scoreEdit.toString().substring(0,1));
    if (!this.scoreMaxEdit) {
      this.scoreMaxEditNull = true;
      this.scoreMaxEditFalse = false;
      this.showEditButton = true;
    } else if (this.scoreMaxEdit <= 0 || this.scoreMaxEdit > 1 || isNaN(this.scoreMaxEdit) ||
      this.scoreMaxEdit.toString().trim().substring(0, 1) == '.' || this.scoreMaxEdit.toString().trim().substring(0, 2) == '0.') {
      console.log('ngoc');
      for (let i = 0; i < this.scoreMaxEdit.length; i++) {
        if (this.scoreMaxEdit[i] == ' ') {
          this.maxLengthScore = this.maxLengthScore + 1;
        }
      }
      this.scoreMaxEditFalse = true;
      this.scoreMaxEditNull = false;
      this.showEditButton = true;
      if (this.scoreMaxEdit.toString().trim().substring(0, 2) == '0.' && this.scoreMaxEdit > 0) {
        // this.maxLengthScore = this.maxLengthScore;
        this.scoreMaxEditFalse = false;
        this.scoreMaxEditNull = false;
        this.showEditButton = false;
      }
    } else {
      this.scoreMaxEditFalse = false;
      this.scoreMaxEditNull = false;
      this.showEditButton = false;
    }
  }

  clickSave(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    });
  }

  closeModal() {
    this.mobjModalRef.hide();
  }

  staffChangeEdit(value: any) {
    this.staffEdit = value;
  }

  dateChangeFromEdit(value: any) {
    if (!value.value) {
      if (!value.targetElement.value) {
        this.dateFromEditNull = true;
        this.dateFromEditInvalid = false;
        this.dateFromEditFalse = false;
        this.showEditButton = true;
        // console.log('date to add null');
      } else if (value.targetElement.value) {
        this.dateFromEditNull = false;
        this.dateFromEditInvalid = true;
        this.dateFromEditFalse = false;
        this.showEditButton = true;
        // console.log('date to add null');
      }
      return;
    }
    // console.log('date from add: ', value.value._d.getTime());
    const dateFromEdit = value.value._d.getTime();
    if (dateFromEdit < this.dateNow - 86400000) {
      this.dateFromEditNull = false;
      this.dateFromEditFalse = true;
      this.dateFromEditInvalid = false;
      this.showEditButton = true;
      console.log('date add from ko hop le');
    } else {
      this.dateFromEditNull = false;
      this.dateFromEditFalse = false;
      this.dateFromEditInvalid = false;
      this.mdtDateFromEdit = value;
      this.showEditButton = false;
    }
  }

  saveEdit() {
    let staffCodeEdit;
    if (this.staffEdit === 'null') {
      staffCodeEdit = null;
    } else {
      staffCodeEdit = this.staffEdit;
    }
    let vdsChannelCode;
    if (this.vdsChannelCode === 'null') {
      vdsChannelCode = null;
    } else {
      vdsChannelCode = this.vdsChannelCode;
    }

    // if (this.scoreEdit === 0) {
    //   this.scoreEditNull = false;
    //   // this.scoreAddInvalid = false;
    //   this.scoreEditFalse = true;
    //   this.showEditButton = true;
    //   return;
    // }
    this.errorEdit = false;
    let serviceModel: ServiceScoreService;
    if (!this.mdtDateToEdit.value) {
      serviceModel = new ServiceScoreService(this.serviceIdEdit, this.servicenameEdit, this.statusEdit,
        this.mdtDateFromEdit.value._d.getTime(), null, this.scoreEdit, this.scoreMaxEdit,
        this.idRowEdit, this.shopCode, staffCodeEdit, vdsChannelCode);
    } else {
      serviceModel = new ServiceScoreService(this.serviceIdEdit, this.servicenameEdit, this.statusEdit,
        this.mdtDateFromEdit.value._d.getTime(), this.mdtDateToEdit.value._d.getTime(), this.scoreEdit, this.scoreMaxEdit,
        this.idRowEdit, this.shopCode, staffCodeEdit, vdsChannelCode);
    }
    console.log('form edit: ', serviceModel);
    this.serviceService.updateScoreService(serviceModel, this.idRowEdit).subscribe(
      data => {
        console.log('edit: ', data);
        if (data['data'] == 'DUPLICATE') {
          this.toastr.error(this.translate.instant('management.weight.duplicate'),
            this.translate.instant('management.weight.infor'), {
            timeOut: 3000,
            positionClass: 'toast-top-center',
          });
        } else if (data['data'] == 'STAFF_NOT_IN_SHOP') {
          this.toastr.error(this.translate.instant('management.weight.staffNotUnit'),
            this.translate.instant('management.weight.infor'), {
            timeOut: 3000,
            positionClass: 'toast-top-center',
          });
        } else {
          this.mobjModalRef.hide();
          this.setFormAdd();
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

        // createModal

        this.mobjModalRef.hide();
      }
    );
  }
}

