import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { filter } from 'rxjs/operators';
import { TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr';
import { ConfigRoleObjectService } from './../../../../services/management/config-role-object.service';
import { ConfigObjectRole } from './../../../../models/ConfigObecjtRole.model';
import { ConfigobjectService } from './../../../../services/management/configobject.service';
import { MatDialog, MatDialogRef } from '@angular/material';
import { TreeviewConfig, TreeviewItem } from 'ngx-treeview';
import { FormGroup, FormBuilder, Validators, FormControl } from '@angular/forms';
import { Component, OnInit, ViewChild, ViewEncapsulation, TemplateRef } from '@angular/core';
import { ObjectConfig } from '../../../../models/objectconfig';
import { WarningReceiveService } from '../../../../services/management/warning-receive.service';

@Component({
  selector: 'app-create-role',
  templateUrl: './create-role.component.html',
  styleUrls: ['./create-role.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class CreateRoleComponent implements OnInit {

  mstrServiceCode = '';
  data;
  objectRole: ObjectConfig[] = [];
  newRoleForm: FormGroup;
  conflictCode: boolean;
  mobjModalRef: BsModalRef;
  mstrServiceName = '';
  mblnServiceName: boolean;
  mstrServiceDes = '';
  mblnServiceDes: boolean;

  constructor(private fb: FormBuilder,
    public dialog: MatDialog,
    private dialogRef: MatDialogRef<CreateRoleComponent>,
    private objectService: ConfigobjectService,
    private configRole: ConfigRoleObjectService,
    private toastr: ToastrService,
    private translate: TranslateService,
    private modalService: BsModalService,
    private warningreceive: WarningReceiveService
  ) {  dialogRef.disableClose = true; }


  ngOnInit() {
    this.createRoleFrom();

    this.objectService.getAllActive().subscribe(data => {
      this.data = data.data;
      this.data = this.data.filter(item => item.parentId != null);
    });

  }

  createRoleFrom() {
    this.newRoleForm = this.fb.group({
      code: ['', [Validators.required, Validators.pattern('^\\s{0,10}?[_A-Za-z0-9]{1,50}\\s{0,10}$')]],
      description: ['', [Validators.required]],
      name: ['', [Validators.required]],
      status: [1, [Validators.required]]
    });
  }

  hasError(controlName: string, errorName: string) {
    return this.newRoleForm.controls[controlName].hasError(errorName);
  }

  createRole() {

    if (this.mblnServiceDes) {
      this.mblnServiceDes = true;
      this.mobjModalRef.hide();
      return;
    }

    if (this.mblnServiceName) {
      this.mblnServiceName = true;
      this.mobjModalRef.hide();
      return;
    }

    if (this.conflictCode) {
      this.conflictCode = true;
      this.mobjModalRef.hide();
      return;
    }

    const configObjectRole = new ConfigObjectRole();
    configObjectRole.roleCode = this.newRoleForm.get('code').value.toString().trim();
    configObjectRole.roleDescription = this.newRoleForm.get('description').value.toString().trim();
    configObjectRole.roleName = this.newRoleForm.get('name').value.toString().trim();
    configObjectRole.status = 1;

    this.configRole.createRole(configObjectRole).subscribe(data => {
      this.showSuccess(this.translate.instant('management.warningconfig.addsuccess'));
      this.warningreceive.setReload(null);
      this.mobjModalRef.hide();
    });

    this.createRoleFrom();

  }

  selectUnit($event: any[]) {
  }

  close() {
    this.dialogRef.close();
  }

  checkCodeConflict() {
    this.mstrServiceCode = this.mstrServiceCode.toUpperCase();
    this.configRole.getAllRoles().subscribe((res: any) => {
      for (let i = 0; i < res.data.length; i++) {
        if (this.mstrServiceCode.trim().toUpperCase().replace(/ /g, '') === res.data[i].roleCode) {
          this.conflictCode = true;
          break;
        } else {
          this.conflictCode = false;
        }
      }
    });
  }

  showSuccess(message: string) {
    this.toastr.success(message,
      this.translate.instant('management.service.message.success'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }

  onBackConfirm() {
    this.mobjModalRef.hide();
  }

  confirmEdit(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }

  checkNameEmpty() {
    if (this.newRoleForm.get('name').value.toString().trim() == '' && this.newRoleForm.get('name').value != '') {
      this.mblnServiceName = true;
    } else {
      this.mblnServiceName = false;
    }
  }

  checkDesEmpty() {
    if (this.newRoleForm.get('description').value.toString().trim() == '' && this.newRoleForm.get('description').value != '') {
      this.mblnServiceDes = true;
    } else {
      this.mblnServiceDes = false;
    }
  }

}
