import { ConfigRoleObjectDTO } from './../../../../models/ConfigRoleObjectDTO.model';
import { ConfigRole } from './../../../../models/ConfigRole.model';
import { ObjectConfigModel } from './../../../../models/objectconfig';
import { TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr';
import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { ConfigObjectRole } from './../../../../models/ConfigObecjtRole.model';
import { EditRoleComponent } from './../edit-role/edit-role.component';
import { ConfigobjectService } from './../../../../services/management/configobject.service';
import { FormGroup, FormBuilder } from '@angular/forms';
import { CreateRoleComponent } from './../create-role/create-role.component';
import { ConfigRoleObjectService } from './../../../../services/management/config-role-object.service';
import { Component, OnInit, ViewChild, ViewEncapsulation, TemplateRef } from '@angular/core';
import { MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';
import { ObjectConfig } from '../../../../models/objectconfig';
import { WarningReceiveService } from '../../../../services/management/warning-receive.service';
// import { Options, Node } from 'ng-material-treetable';

@Component({
  selector: 'app-list-role',
  templateUrl: './list-role.component.html',
  styleUrls: ['./list-role.component.scss'],
  providers: [
    { provide: MatDialogRef, useValue: { CreateRoleComponent } },
    { provide: MatDialogRef, useValue: { EditRoleComponent } }
  ],
  encapsulation: ViewEncapsulation.None
})
export class ListRoleComponent implements OnInit {

  p;

  marrRoles: ConfigRole[] = [];
  marrRolesLock: ConfigRole[] = [];
  marrObject: ConfigRoleObjectDTO[] = [];
  codeSearch: string;
  codeObjectSearch: string;
  createForm: FormGroup;
  objectRole: ObjectConfig[] = [];
  nameAllObject: string;
  mblUnlockHidden: boolean;
  mblLockHidden: boolean;
  mblCheckInPageOrAll: boolean;
  checkBoxInPage: boolean;
  mobjModalRef: BsModalRef;
  recordInPage: number;
  recordInPageMin: number;
  itemPerpage = 10;
  marrRoleInPage: ConfigRole[] = [];
  roleIDChoose: number;
  actionString = '';
  roleCodeAndName = '';
  disableObject: boolean;
  select: number;
  checkedAllCreate: boolean;
  checkedAllRead: boolean;
  checkedAllUpdate: boolean;
  checkedAllDelete: boolean;
  roleChoose: any;
  reload = false;

  constructor(
    private configRole: ConfigRoleObjectService,
    public dialog: MatDialog,
    private modalService: BsModalService,
    private toastr: ToastrService,
    private translate: TranslateService,
    private warningreceive: WarningReceiveService
  ) { }

  ngOnInit() {
    this.checkBoxInPage = false;
    this.codeSearch = '';
    this.codeObjectSearch = '';
    this.roleIDChoose = -1;

    this.configRole.getActionOfRole(this.roleIDChoose).subscribe((data: any) => {
      this.marrObject = data.data;
    });
    this.searchByCondition();
    this.warningreceive.reload$.subscribe(res => {
      this.reload = true;
    })
    this.searchByCondition();
    this.reload = false;
    setTimeout( () => {
    }, 2000);

  }

  /**
   * Tìm kiếm theo điều kiện
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  searchByCondition() {
    this.roleIDChoose = -1;
    this.configRole.getRoleByCondition(this.codeSearch.trim(), this.codeObjectSearch.trim()).subscribe(data => {
      this.marrRoles = data.data;
      this.configRole.getActionOfRole(this.roleIDChoose).subscribe((item: any) => {
        this.marrObject = item.data;
        this.roleCodeAndName = '';
        this.checkBoxInPage = false;
        this.marrRolesLock = [];
        this.checkedAllCreate = false;
        this.checkedAllRead = false;
        this.checkedAllUpdate = false;
        this.checkedAllDelete = false;
        this.mblUnlockHidden = false;
        this.mblLockHidden = false;
        this.select = 0;
      });
    });
  }

  /**
   * Mở dialog thêm mới
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  openDialog() {

    const dialogRef = this.dialog.open(CreateRoleComponent, {
      maxWidth: '75vw',
      maxHeight: '45vh',
      width: '70vw',
    });

    dialogRef.afterClosed().subscribe(result => {
      if (this.reload) {
        this.searchByCondition();
        this.reload = false;
      }
    });
  }

  /**
   * Mở dialog sửa
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  openDialogEdit(role) {

    const dialogRef = this.dialog.open(EditRoleComponent, {
      maxWidth: '75vw',
      maxHeight: '45vh',
      width: '70vw',
      data: { role: role }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (this.reload) {
        this.searchByCondition();
        this.reload = false;
      }
    });
  }

  /**
   * Mở dialog xác nhận mở khóa
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  confirmUnLock(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
        ignoreBackdropClick: true
      }
    );
  }

  /**
   * Mở dialog xác nhận khóa
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  confirmLock(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
        ignoreBackdropClick: true
      }
    );
  }

  /**
   * Mở dialog xác nhận mở khóa
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  confirmSave(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
        ignoreBackdropClick: true
      }
    );
  }

  /**
   * Đóng dialog xác nhận mở khóa
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  onBackConfirm() {
    this.mobjModalRef.hide();
  }

  /**
   * Checkbox in page
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  checkInPage() {
    this.marrRolesLock = [];

    if (this.checkBoxInPage === false) {
      this.checkBoxInPage = true;

      this.marrRolesLock = this.marrRoles.slice();

      this.marrRoles.forEach(data => {
        data.check = true;
      });

      this.mblLockHidden = true;
      this.mblUnlockHidden = true;
    } else {
      this.checkBoxInPage = false;
      this.marrRolesLock.forEach(data => {
        data.check = false;
      });

      this.marrRoles.forEach(item => {
        item.check = false;
      });

      this.marrRoleInPage = [];
      this.mblLockHidden = false;
      this.mblUnlockHidden = false;
    }

  }

  /**
   * Checkbox từng role
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  checkUnit(unit) {

    if (unit.check !== true) {
      unit.check = true;
      this.marrRolesLock.push(unit);
      if (this.marrRolesLock.length === this.marrRoles.length) {
        this.checkBoxInPage = true;
      }
    } else {
      unit.check = false;
      this.checkBoxInPage = false;
      this.marrRolesLock = this.marrRolesLock.filter(item => item.id !== unit.id);
    }
    this.mblUnlockHidden = false;
    this.mblLockHidden = false;

    this.marrRoles.forEach(role => {
      if (role.check) {
        if (role.status == 0) {
          this.mblUnlockHidden = true;
        } else {
          this.mblLockHidden = true;
        }
      }
    });



  }

  /**
   * Khóa hoặc mở
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  lockOrUnlock(status) {
    if (this.marrRolesLock.length > 0) {
      for (let i = 0; i < this.marrRolesLock.length; i++) {
        this.marrRolesLock[i].status = status;
        this.configRole.editRole(this.marrRolesLock[i]).subscribe(data => {
          if (i === this.marrRolesLock.length - 1) {
            this.configRole.getAllRoles().subscribe(item => {
              this.marrRoles = item.data;
            });
          }
        });
      }
    }

    if (status === 1) {
      this.mobjModalRef.hide();
      this.showSuccess(this.translate.instant('management.service.message.successUnlock'));
    } else {
      this.mobjModalRef.hide();
      this.showSuccess(this.translate.instant('management.service.message.successLock'));
    }

    this.mblUnlockHidden = false;
    this.mblLockHidden = false;
    this.checkBoxInPage = false;

    this.marrRolesLock.forEach(item => {
      item.check = false;
    });
    this.marrRolesLock = [];
  }

  /**
   * Hiển thị thông báo thành công
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  showSuccess(message: string) {
    this.toastr.success(message,
      this.translate.instant('management.service.message.success'), {
        timeOut: 2000,
        positionClass: 'toast-top-center',
      });
  }

  /**
   * Hiển thị thông báo không thành công
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  showError(message: string) {
    this.toastr.error(message,
      this.translate.instant('management.service.message.fail'), {
        timeOut: 2000,
        positionClass: 'toast-top-center',
      });
  }

  /**
   * Click role show object owner
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  showObjectOfRole(role) {
    this.select = role.id;
    this.roleIDChoose = role.id;
    this.roleChoose = role;
    this.roleCodeAndName = ' : ' + role.roleCode + '-' + role.roleName;
    this.getActionRole(role);
  }

  getActionRole(role) {
    let lengthOfCheckboxCreate = 0;
    let lengthOfCheckboxRead = 0;
    let lengthOfCheckboxUpdate = 0;
    let lengthOfCheckboxDelete = 0;
    this.configRole.getActionOfRole(role.id).subscribe((data: any) => {
      this.marrObject = data.data;
      this.marrObject.forEach(item => {
        if (item.isDefault === 1) {
          item.checkboxDefault = true;
        } else {
          item.checkboxDefault = false;
        }

        if (item.create) {
          lengthOfCheckboxCreate++;
        }

        if (item.read) {
          lengthOfCheckboxRead++;
        }

        if (item.update) {
          lengthOfCheckboxUpdate++;
        }

        if (item.delete) {
          lengthOfCheckboxDelete++;
        }

      });
      if (role.status === 1) {
        this.disableObject = false;
      } else {
        this.disableObject = true;
      }

      if (lengthOfCheckboxCreate === this.marrObject.length) {
        this.checkedAllCreate = true;
      } else {
        this.checkedAllCreate = false;
      }

      if (lengthOfCheckboxRead === this.marrObject.length) {
        this.checkedAllRead = true;
      } else {
        this.checkedAllRead = false;
      }

      if (lengthOfCheckboxUpdate === this.marrObject.length) {
        this.checkedAllUpdate = true;
      } else {
        this.checkedAllUpdate = false;
      }

      if (lengthOfCheckboxDelete === this.marrObject.length) {
        this.checkedAllDelete = true;
      } else {
        this.checkedAllDelete = false;
      }

    });
  }

  /**
   * Check only one default
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  checkDefault(idObject) {
    this.marrObject.forEach(item => {
      if (item.objectId === idObject) {
        if (item.checkboxDefault === true) {
          item.checkboxDefault = false;
        } else {
          item.checkboxDefault = true;
        }
      } else {
        item.checkboxDefault = false;
      }
    });
  }

  checkAllActionCreate() {
    if (!this.checkedAllCreate) {
      this.marrObject.forEach(item => {
        item.create = true;
      });
      this.checkedAllCreate = true;
    } else {
      this.marrObject.forEach(item => {
        item.create = false;
        if (item.checkboxDefault === true) {
          if (!item.create && !item.read && !item.update && !item.delete) {
            item.checkboxDefault = false;
          }
        }
      });
      this.checkedAllCreate = false;
    }
  }

  checkAllActionRead() {
    if (!this.checkedAllRead) {
      this.marrObject.forEach(item => {
        item.read = true;
      });
      this.checkedAllRead = true;
    } else {
      this.marrObject.forEach(item => {
        item.read = false;
        if (item.checkboxDefault === true) {
          if (!item.create && !item.read && !item.update && !item.delete) {
            item.checkboxDefault = false;
          }
        }
      });
      this.checkedAllRead = false;
    }
  }

  checkAllActionUpdate() {
    if (!this.checkedAllUpdate) {
      this.marrObject.forEach(item => {
        item.update = true;
      });
      this.checkedAllUpdate = true;
    } else {
      this.marrObject.forEach(item => {
        item.update = false;
        if (item.checkboxDefault === true) {
          if (!item.create && !item.read && !item.update && !item.delete) {
            item.checkboxDefault = false;
          }
        }
      });
      this.checkedAllUpdate = false;
    }
  }

  checkAllActionDelete() {
    if (!this.checkedAllDelete) {
      this.marrObject.forEach(item => {
        item.delete = true;
      });
      this.checkedAllDelete = true;
    } else {
      this.marrObject.forEach(item => {
        item.delete = false;
        if (item.checkboxDefault === true) {
          if (!item.create && !item.read && !item.update && !item.delete) {
            item.checkboxDefault = false;
          }
        }
      });
      this.checkedAllDelete = false;
    }
  }

  /**
   * Check create checkbox
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  changeActionC(object) {
    let lengthOfCheckboxRead = 0;
    this.marrObject.forEach(item => {
      if (item.objectId === object.objectId) {
        if (item.create === true) {
          item.create = false;
        } else {
          item.create = true;
        }

        if (!item.create && !item.read && !item.update && !item.delete) {
          item.checkboxDefault = false;
        }
      }

      if (item.create === true) {
        lengthOfCheckboxRead++;
      }
    });

    if (lengthOfCheckboxRead === this.marrObject.length) {
      this.checkedAllCreate = true;
    } else {
      this.checkedAllCreate = false;
    }
  }


  /**
   * Check read checkbox
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  changeActionR(object) {
    let lengthOfCheckboxRead = 0;
    this.marrObject.forEach(item => {
      if (item.objectId === object.objectId) {
        if (item.read === true) {
          item.read = false;
        } else {
          item.read = true;
        }

        if (!item.create && !item.read && !item.update && !item.delete) {
          item.checkboxDefault = false;
        }

      }

      if (item.read === true) {
        lengthOfCheckboxRead++;
      }

    });

    if (lengthOfCheckboxRead === this.marrObject.length) {
      this.checkedAllRead = true;
    } else {
      this.checkedAllRead = false;
    }
  }

  /**
   * Check update checkbox
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  changeActionU(object) {
    let lengthOfCheckboxRead = 0;
    this.marrObject.forEach(item => {
      if (item.objectId === object.objectId) {
        if (item.update === true) {
          item.update = false;
        } else {
          item.update = true;
        }

        if (!item.create && !item.read && !item.update && !item.delete) {
          item.checkboxDefault = false;
        }
      }

      if (item.update === true) {
        lengthOfCheckboxRead++;
      }
    });

    if (lengthOfCheckboxRead === this.marrObject.length) {
      this.checkedAllUpdate = true;
    } else {
      this.checkedAllUpdate = false;
    }
  }

  /**
   * Check delete checkbox
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  changeActionD(object) {
    let lengthOfCheckboxRead = 0;
    this.marrObject.forEach(item => {
      if (item.objectId === object.objectId) {
        if (item.delete === true) {
          item.delete = false;
        } else {
          item.delete = true;
        }

        if (!item.create && !item.read && !item.update && !item.delete) {
          item.checkboxDefault = false;
        }
      }

      if (item.delete === true) {
        lengthOfCheckboxRead++;
      }
    });

    if (lengthOfCheckboxRead === this.marrObject.length) {
      this.checkedAllDelete = true;
    } else {
      this.checkedAllDelete = false;
    }
  }

  /**
   * Save object
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  saveObject() {
    const marrObject: ConfigRoleObjectDTO[] = [];
    // let haveDefault = true;
    //
    // for (let i = 0; i < this.marrObject.length; i++) {
    //   if (this.marrObject[i].checkboxDefault === true) {
    //     haveDefault = true;
    //     break;
    //   } else {
    //     haveDefault = false;
    //   }
    // }
    //
    // if (haveDefault) {
    this.marrObject.forEach(data => {
      if (data.create || data.read || data.update || data.delete) {
        data.action = '';
        if (data.create) {
          data.action = data.action + 'C,';
        }

        if (data.read) {
          data.action = data.action + 'R,';
        }

        if (data.update) {
          data.action = data.action + 'U,';
        }

        if (data.delete) {
          data.action = data.action + 'D,';
        }

        data.action = data.action.substring(0, data.action.length - 1);
        data.roleId = this.roleIDChoose;
        data.status = 1;
        if (data.checkboxDefault === true) {
          data.isDefault = 1;
        } else {
          data.isDefault = 0;
        }

        marrObject.push(data);
      }

      if (!data.create && !data.read && !data.update && !data.delete && data.id != null) {
        data.status = 0;
        data.action = null;
        data.isDefault = 0;
        marrObject.push(data);
      }
    });

    this.configRole.createRoleObject(marrObject).subscribe(data => {
      this.configRole.getActionOfRole(this.roleIDChoose).subscribe((item: any) => {
        this.marrObject = item.data;
        this.marrObject.forEach(object => {
          if (object.isDefault === 1) {
            object.checkboxDefault = true;
          } else {
            object.checkboxDefault = false;
          }
        });
        this.mobjModalRef.hide();
        this.showSuccess(this.translate.instant('management.warningconfig.updatesuccess'));
      });
    });
    // } else {
    //   this.mobjModalRef.hide();
    //   this.showError(this.translate.instant('management.objectrole.errorDefault'));
    // }
  }

  /**
   * Cancel button. Reset
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  cancel() {
    this.getActionRole(this.roleChoose);
  }

}

