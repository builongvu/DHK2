import { config } from './../../config/application.config';
import { DVT, DVTDTO } from './../../models/dvt.model';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UnitModel } from '../../models/unit.model';

@Injectable()
export class UnitService {

  constructor(private http: HttpClient) {
  }

  getUnitsLevel(): Observable<UnitModel[]> {
    return this.http.post<UnitModel[]>(config.unit_level_API, null);
  }

  getUnitsDashboard(groupId: number): Observable<UnitModel[]> {
    const url = config.unit_dashboard_API + '?groupId=' + groupId;
    return this.http.post<UnitModel[]>(url, null);
  }

  getAllUnit(): Observable<DVT[]> {
    return this.http.get<DVT[]>(`${config.unit_API}`);
  }

  getUnit(): Observable<DVT[]> {
    return this.http.get<DVT[]>(`${config.unit_All_API}`);
  }

  getByID(id: number): Observable<DVT[]> {
    return this.http.get<DVT[]>(`${config.getUnitByID}/${id}`);
  }

  getUnitByCondition(unitDTO: DVTDTO): Observable<DVT[]> {
    return this.http.post<DVT[]>(`${config.unit_search_API}`, unitDTO);
  }

  getActiveUnits(): Observable<UnitModel[]> {
    return this.http.get<UnitModel[]>(config.active_unit_API);
  }

  getUnitsReport(): Observable<UnitModel[]> {
    const url = `${config.unit_report_API}`;
    return this.http.post<UnitModel[]>(url, null);
  }

  saveUnit(unit: DVT, type: number) {
    if (type == 1) {
      return this.http.post<any>(config.saveNewUnit, unit);
    }
    return this.http.post<any>(config.updateNewUnit, unit);
  }

  lockUnlock(id: string[], status: string){
    return this.http.post<any>(config.changeStatusUnit + '/' + status, id);
  }
}
