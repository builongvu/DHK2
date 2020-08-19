import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { config } from '../../config/application.config';
import { StaffModel } from '../../models/staff.model';

@Injectable()
export class RoleStaffService {

  constructor(private http: HttpClient) {
  }

  getAllRoleStaff() {
    return this.http.get<any>(config.apiUrl + "/management/rolestaff/getAllActive");
  }

  saveRoleStaff(staffcode: String, marr) {
    return this.http.post<any>(config.apiUrl + "/management/rolestaff/saveAll/" + staffcode, marr);
  }

  getByStaffCode(staffcode: String) {
    return this.http.get<any>(config.getByStaffCode_API + "/" + staffcode);
  }
}
