import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {config} from '../../config/application.config';
import {StaffModel, StaffDTO} from '../../models/staff.model';

@Injectable()
export class StaffService {

  constructor(private http: HttpClient) {
  }

  getStaffs(): Observable<StaffModel[]> {
    return this.http.get<StaffModel[]>(config.staff_API);
  }

  getAllStaffs(){
    return this.http.get<any>(config.getAllStaff_API);
  }

  getSearchStaffs(staff: StaffDTO){
    return this.http.post<any>(config.getSearchStaff_API, staff);
  }
}
