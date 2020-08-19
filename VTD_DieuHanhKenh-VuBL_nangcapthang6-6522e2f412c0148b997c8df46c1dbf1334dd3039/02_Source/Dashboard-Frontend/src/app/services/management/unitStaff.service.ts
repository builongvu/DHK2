import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Injectable} from '@angular/core';

import {config} from '../../config/application.config';
import {UnitStaffModel} from '../../models/UnitStaff.model';

@Injectable()
export class UnitStaffService {

  constructor(private http: HttpClient) {
  }

  getUnitStaff(): Observable<UnitStaffModel[]> {
    return this.http.get<UnitStaffModel[]>(config.unitTree_API);
  }
}
