import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { ConfigObjectRole } from './../../../../models/ConfigObecjtRole.model';
import { Component, OnInit, Inject, AfterViewChecked, ChangeDetectorRef, TemplateRef, ViewEncapsulation } from '@angular/core';
import { FormBuilder, Validators, FormGroup } from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { CreateRoleComponent } from '../create-role/create-role.component';
import { ConfigobjectService } from '../../../../services/management/configobject.service';
import { ConfigRoleObjectService } from '../../../../services/management/config-role-object.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { ObjectConfig } from '../../../../models/objectconfig';
import { WarningReceiveService } from '../../../../services/management/warning-receive.service';

@Component({
  selector: 'app-edit-role',
  templateUrl: './edit-role.component.html',
  styleUrls: ['./edit-role.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class EditRoleComponent implements OnInit, AfterViewChecked {


  mstrServiceCode = '';
  data;
  objectRole: ObjectConfig[] = [];
  objects: ObjectConfig[] = [];
  newRoleForm: FormGroup;
  conflictCode: boolean;
  strIntoObj: string[] = [];
  arrObject: number[] = [];
  mobjModalRef: BsModalRef;
  mblnServiceName: boolean;
  mblnServiceDes: boolean;


  constructor(private fb: FormBuilder,
    public dialogRef: MatDialogRef<EditRoleComponent>,
    @Inject(MAT_DIALOG_DATA) public role,
    public dialog: MatDialog,
    private objectService: ConfigobjectService,
    private configRole: ConfigRoleObjectService,
    private toastr: ToastrService,
    private translate: TranslateService,
    private cdRef: ChangeDetectorRef,
    private modalService: BsModalService,
    private warningreceive: WarningReceiveService
  ) { dialogRef.disableClose = true; }


  ngAfterViewChecked(): void {
    this.mstrServiceCode = this.newRoleForm.get('code').value;
    this.cdRef.detectChanges();
  }

  ngOnInit() {
    this.createRoleFrom();
    this.objectService.getAllActive().subscribe(data => {
      this.objects = data.data;
      this.objects = this.objects.filter(item => item.parentId != null);
    });



    this.mstrServiceCode = this.role.role.roleCode;

    // this.strIntoObj = JSON.parse(this.role.role.mobjConfigRolesObjects);
  }


  /**
* Create reactive form
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  createRoleFrom() {

    this.newRoleForm = this.fb.group({
      code: [{ value: this.role.role.roleCode, disabled: true },
      [Validators.required, Validators.pattern('^\\s{0,10}?[_A-Za-z0-9]{1,50}\\s{0,10}$')]],
      description: [this.role.role.roleDescription, [Validators.required]],
      name: [this.role.role.roleName, [Validators.required]],
      status: [this.role.role.status, [Validators.required]]
    });
  }

  /**
* check error reactive form
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  hasError(controlName: string, errorName: string) {
    return this.newRoleForm.controls[controlName].hasError(errorName);
  }


  close() {
    this.dialogRef.close();
  }

  /**
* Edit role
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  editRole() {

    if (this.mblnServiceName) {
      this.mobjModalRef.hide();
      this.mblnServiceName = true;
      return;
    }

    if (this.mblnServiceDes) {
      this.mobjModalRef.hide();
      this.mblnServiceDes = true;
      return;
    }

    // if (this.conflictCode) {
    //   this.mobjModalRef.hide();
    //   this.conflictCode = true;
    //   return;
    // }

    const configObjectRole = new ConfigObjectRole();
    configObjectRole.id = this.role.role.id;
    configObjectRole.roleCode = this.newRoleForm.get('code').value.toString().trim();
    configObjectRole.roleDescription = this.newRoleForm.get('description').value.toString().trim();
    configObjectRole.roleName = this.newRoleForm.get('name').value.toString().trim();
    configObjectRole.status = this.newRoleForm.get('status').value.toString().trim();

    this.configRole.editRole(configObjectRole).subscribe(data => {
      this.showSuccess(this.translate.instant('management.warningconfig.updatesuccess'));
      this.mobjModalRef.hide();
      this.warningreceive.setReload(null);
      this.dialogRef.close();
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

  /**
* Check code conflict
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  checkCodeConflict() {
    this.mstrServiceCode = this.mstrServiceCode.toUpperCase();
    this.configRole.getAllRoles().subscribe((res: any) => {
      if (this.mstrServiceCode.trim().toUpperCase().replace(/ /g, '') !== this.role.role.roleCode) {
        for (let i = 0; i < res.data.length; i++) {
          if (this.mstrServiceCode.trim().toUpperCase().replace(/ /g, '') === res.data[i].roleCode) {
            this.conflictCode = true;
            break;
          } else {
            this.conflictCode = false;
          }
        }
      } else {
        this.conflictCode = false;
      }
    });
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
