import { Component, OnInit, TemplateRef } from '@angular/core';
import { RoleStaffService } from '../../../services/management/role-staff.service';
import { ConfigRoleObjectService } from '../../../services/management/config-role-object.service';
import { ConfigRole } from '../../../models/ConfigRole.model';
import { StaffService } from '../../../services/management/staff.service';
import { StaffModel, StaffDTO } from '../../../models/staff.model';
import { ShopService } from '../../../services/management/shop.service';
import { Shop } from '../../../models/Shop.model';
import { RoleStaffDTO } from '../../../models/rolestaff';
import { BsModalService, BsModalRef } from 'ngx-bootstrap';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-role-staff',
  templateUrl: './role-staff.component.html',
  styleUrls: ['./role-staff.component.scss']
})
export class RoleStaffComponent implements OnInit {
  marrRoles: RoleStaffDTO[] = [];
  marrStaffs: StaffDTO[] = [];
  marrShop: Shop[] = [];
  mobjStaff: StaffDTO = new StaffDTO();
  mobjSearchStaff: StaffDTO = new StaffDTO();
  mobjModalRef: BsModalRef;
  select: number;
  checksave = 0;
  mblncheckAll = false;
  constructor(
    private roleStaffService: RoleStaffService,
    private staffService: StaffService,
    private shopService: ShopService,
    private toastr: ToastrService,
    private translate: TranslateService,
    private modalService: BsModalService
  ) { }

  ngOnInit() {
    this.showRoleOfStaff(this.mobjStaff);
    this.mobjSearchStaff.isHaveRole = 1;
    this.Search();
  }


  showRoleOfStaff(staff: StaffDTO) {
    this.select = staff.id;
    this.roleStaffService.getByStaffCode(staff.code).subscribe(res => {
      this.marrRoles = res.data;
      this.mobjStaff = staff;
      this.checkSave();
    });
  }

  checkAll() {
    this.marrRoles.forEach(role => {
      if (this.mblncheckAll) {
        role.check = true;
      } else {
        role.check = false;
      }
    });
    this.checkSave();
  }

  resetrole() {
    if (this.mobjSearchStaff.isHaveRole == 0) {
      this.mobjSearchStaff.role = '';
    }
  }

  clickSearch() {
    this.mobjStaff = new StaffDTO();
    this.mblncheckAll = false;
    this.Search();
  }

  Search() {
    if (this.mobjSearchStaff.code) {
      this.mobjSearchStaff.code = this.mobjSearchStaff.code.trim();
    }
    if (this.mobjSearchStaff.shopcode) {
      this.mobjSearchStaff.shopcode = this.mobjSearchStaff.shopcode.trim();
    }
    if (this.mobjSearchStaff.role) {
      this.mobjSearchStaff.role = this.mobjSearchStaff.role.trim();
    }
    this.staffService.getSearchStaffs(this.mobjSearchStaff).subscribe(res => {
      this.marrStaffs = res.data;
      this.shopService.getAll().subscribe(respon => {
        this.marrShop = respon.data;
        if (!this.marrStaffs.filter(item => item.id == this.select)[0]) {
          this.mobjStaff = new StaffDTO();
        }
        this.showRoleOfStaff(this.mobjStaff);
      });
    });
  }

  confirmSave(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }

  checkSave() {
    let marr: string[] = [];
    this.marrRoles.forEach(role => {
      if (role.check) {
        marr.push(role.id.toString())
      }
    });
    if (this.mobjSearchStaff.isHaveRole == 0 && marr.length == 0) {
      this.checksave = 0;
    } else {
      this.checksave = 1;
    }
    if (this.mobjStaff.id) {
      if(marr.length == this.marrRoles.length) {
        this.mblncheckAll = true;
      }else{
        this.mblncheckAll = false;
      }
    }
  }

  save() {
    let marr: string[] = [];
    this.marrRoles.forEach(role => {
      if (role.check) {
        marr.push(role.id.toString())
      }
    });
    this.roleStaffService.saveRoleStaff(this.mobjStaff.code, marr).subscribe(res => {
      if (res.code == 200) {
        this.showSuccess(this.translate.instant('management.warningconfig.updatesuccess'));
        this.showRoleOfStaff(this.mobjStaff);
      } else {
        this.showError(res.errors);
      }
      // if(this.mobjSearchStaff.isHaveRole == 0){
      this.Search();
      // }
      this.back();
    });
  }

  back() {
    this.mobjModalRef.hide();
  }

  /**
 * Thông báo thành công
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  showSuccess(message: string) {
    this.toastr.success(message,
      this.translate.instant('management.warningconfig.success'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }

  /**
  * Thông báo thất bại(Lỗi)
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2019/11/18
  */
  showError(message: string) {
    this.toastr.error(message,
      this.translate.instant('management.warningconfig.fail'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }
}
