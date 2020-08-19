import {Component, Inject, OnDestroy, OnInit, TemplateRef} from '@angular/core';
import {VttTargetGroupManagementComponent} from '../vtt-target-group-management.component';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {TreeviewConfig} from 'ngx-treeview';
import {BsModalRef, BsModalService} from 'ngx-bootstrap';
import {GroupsService} from '../../../../services/management/group.service';
import {GroupModel} from '../../../../models/group.model';
import {ToastrService} from 'ngx-toastr';
import {WarningSendService} from '../../../../services/management/warning-send.service';
import {TranslateService} from '@ngx-translate/core';
import { WarningReceive } from '../../../../models/Warning-Receive';
import { WarningReceiveService } from '../../../../services/management/warning-receive.service';

@Component({
  selector: 'app-dialog-group',
  templateUrl: './dialog-group.component.html',
  styleUrls: ['./dialog-group.component.scss']
})
export class DialogGroupComponent implements OnInit, OnDestroy {
  products: any;
  test = true;
  value1: any;
  tree = false;
  isAd = false;
  checkAllNull: boolean;
  preventSpace = false;
  stpcial = '';
  dsb: boolean;
  special = false;
  special1 = false;
  groupMode: GroupModel;
  mobjModalRef: BsModalRef;
  conflictCode: boolean;
  newSerivceGroupForm: FormGroup;
  mstrNewName = '';
  mblnIsValidProductField = true;
  mblnIsValidCodeField = true;
  mblnIsValidNameField = true;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: FormBuilder,
    private groupsService: GroupsService, private modalService: BsModalService,
    private toastr: ToastrService,
    private translate: TranslateService,
    private warningReceiveService: WarningReceiveService,
    public dialogRef: MatDialogRef<VttTargetGroupManagementComponent>
  ) {
    dialogRef.disableClose = true;
  }

  mobjConfig = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 70,
  });
  mstrServiceCode = '';

  //
  colose() {
    this.dialogRef.close();
  }

  ngOnInit() {
    this.products = this.data.products;
    this.groupMode = this.data.groupservice;
    this.dsb = this.data.h;
    if (!this.dsb) {
      if (this.groupMode) {
        this.mstrServiceCode = this.groupMode.code;
        this.mstrNewName = this.groupMode.name;
        this.value1 = this.groupMode.productId;
      }
    }
    this.createNewSerivceGroupForm();
    this.onChanges();
  }

  focusField(pstrField) {
    if (pstrField == 'productField') {
      this.mblnIsValidProductField = true;
    } else if (pstrField == 'codeField') {
      this.mblnIsValidCodeField = true;
    } else if (pstrField == 'nameField') {
      this.mblnIsValidNameField = true;
    }
  }

  Onchange1(value: any) {
    this.newSerivceGroupForm.controls['product'].setValue(value);
    if (value == -1) {
      this.tree = true;
    } else {
      this.tree = false;
    }
  }

  checkCodeConflict(event: any) {
    this.groupsService.getAllGroupService().subscribe((res: any) => {
      let marr: GroupModel[] = res.data;
      marr = marr.filter(item => item.code !== this.groupMode.code);
      this.conflictCode = false;
      for (let i = 0; i < marr.length; i++) {
        if (this.mstrServiceCode.toLocaleUpperCase() === marr[i].code.trim()) {
          this.conflictCode = true;
          break;
        }
      }
    });
    this.stpcial = event.target.value;
    if ((event.target.value || '').trim().length === 0) {
      this.special1 = true;
      this.preventSpace = false;
    } else if (!this.stpcial.match('^[_A-Za-z0-9]{1,50}$')) {
      this.special1 = false;
      this.preventSpace = true;
    } else {
      this.preventSpace = false;
      this.special1 = false;
    }
  }

  onBackConfirm() {
    this.mstrNewName = '';
    this.mstrServiceCode = '';
    this.dialogRef.close();
  }

  confirmAd(pobjTemplate: TemplateRef<any>) {
    if (this.mobjModalRef) {
      this.mobjModalRef.hide();
    }
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
        ignoreBackdropClick: true
      }
    );
  }

  createNewSerivceGroupForm() {
    this.newSerivceGroupForm = this.fb.group({
      product: [''],
      code: [''],
      name: [''],
      status: ['1']
    });
  }

  showSuccess(message: string) {
    this.toastr.success(message,
      this.translate.instant('management.warningconfig.success'), {
        timeOut: 2000,
        positionClass: 'toast-top-center',
      });
  }

  insertData() {
    const message = this.translate.instant('management.group.message.insert');
    const messageSuccess = message + this.translate.instant('management.group.message.success');
    const messageFail = messageSuccess + this.translate.instant('management.group.message.fail');
    // const groupModelN: GroupModel = new GroupModel(this.mstrCodeGroup, this.mstrNameGroup, this.mnbrProductIdcbo);

    this.groupMode.code = (this.newSerivceGroupForm.get('code').value.toString().trim().toUpperCase().replace(/ /g, ''));
    this.groupMode.name = this.newSerivceGroupForm.get('name').value;
    this.groupMode.productId = this.newSerivceGroupForm.get('product').value;
    if (!this.groupMode.id) {
      this.groupsService.insertData(this.groupMode).subscribe(data => {
        this.mobjModalRef.hide();
        this.newSerivceGroupForm.reset();
        this.showSuccess(this.translate.instant('management.group.message.insertSuccess'));
        this.warningReceiveService.setReloadWarning(1);
        this.value1 = -1;
        this.isAd = true;
      });
      this.products = this.data.products;
      this.groupMode = this.data.groupservice;
    } else {
      this.isAd = false;
      this.groupsService.updateData(this.groupMode).subscribe(data => {
        this.mobjModalRef.hide();
        this.dialogRef.close();
        this.warningReceiveService.setReloadWarning(0);
        this.showSuccess(this.translate.instant('management.group.message.updateSuccess'));
      });
    }
  }

  closeModal() {
    this.mobjModalRef.hide();
  }

  checkSpecial(event: any) {
    if ((event.target.value || '').trim().length === 0) {
      this.special = true;
    } else {
      this.special = false;
    }
  }

  // checkAllNullForm() {
  //   const formValue = this.newSerivceGroupForm.value;
  //   const product = formValue.product;
  //   const code = formValue.code;
  //   const name = formValue.name;
  //   if (product === '' && code === '' && name === '') {
  //     this.checkAllNull = true;
  //   } else {
  //     this.checkAllNull = false;
  //   }
  // }

  onChanges(): void {
    this.newSerivceGroupForm.valueChanges.subscribe(val => {
      const product = val.product;
      const code = val.code;
      const name = val.name;
      if (product === '' || code === '' || name === '') {
        this.checkAllNull = true;
      } else {
        this.checkAllNull = false;
      }
    });
  }

  ngOnDestroy(): void {
  }
  check(){
    this.isAd = false;
  }
}
