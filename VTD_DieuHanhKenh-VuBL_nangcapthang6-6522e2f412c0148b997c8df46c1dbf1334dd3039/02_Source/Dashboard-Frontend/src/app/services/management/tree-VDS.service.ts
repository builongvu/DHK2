import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {config} from '../../config/application.config';
import {DeclareVDSModel} from '../../models/declareVDS.model';
import {UnitModel} from '../../models/unit.model';
import {StaffSearchVDS} from '../../models/StaffSearchVDS.model';
import {DelStaff} from '../../models/DelStaff.model';
import {AddStaff} from '../../models/AddStaff.model';
import {EditStaffVDSModel} from '../../models/EditStaffVDS.model';
import {ShopCodeModel} from '../../models/shopCode.model';

@Injectable()
export class TreeVDSService {

  constructor(private http: HttpClient) {
  }

  service: BehaviorSubject<any> = new BehaviorSubject<any>([]);
  service$: Observable<any> = this.service.asObservable();

  reloadTreeAdd: BehaviorSubject<any> = new BehaviorSubject<any>([]);
  reloadTreeAdd$: Observable<any> = this.service.asObservable();

  setUnit(service: any) {
    this.service.next(service);
  }

  getunitsVDS(keySearch: string): Observable<any> {
    return this.http.get<any>(config.search_VDS_API + '?keySearch=' + keySearch);
  }

  addStafftoUnit(staffs: AddStaff): Observable<any> {
    return this.http.post<any>(config.search_VDS_Staff_Create_API, staffs);
  }

  showStaffsTable(shopCode: UnitModel): Observable<any> {
    return this.http.post<any>(config.search_VDS_Staff_Show_API, shopCode);
  }

  searchStaffVDS(StaffSearch: StaffSearchVDS): Observable<any> {
    return this.http.post<any>(config.search_VDS_Staff_Search_API, StaffSearch);
  }

  deleleStaff(idToDel: DelStaff): Observable<any> {
    return this.http.post<any>(config.search_VDS_Staff_Delete_API, idToDel);
  }

  getDeclareDatas(shopCode: string, child: number): Observable<any> {
    return this.http.get<any>(config.data_declare_VDS_API + '?shopCode=' + shopCode + '&child=' + child);
  }

  getGroupVDS(shopCode: string): Observable<any> {
    return this.http.get<any>(config.group_VDS_API + '?shopCode=' + shopCode);
  }

  createVDS(model: DeclareVDSModel) {
    return this.http.post(config.create_declare_VDS_API, model);
  }

  searchVDS(model: DeclareVDSModel): Observable<any> {
    return this.http.post<any>(config.search_form_VDS_API, model);
  }

  updateDeclareVDS(model: DeclareVDSModel) {
    return this.http.post(config.update_declare_VDS_API, model);
  }

  updateStaff(staffUpdate: EditStaffVDSModel): Observable<any> {
    return this.http.post<any>(config.search_VDS_Staff_Update_API, staffUpdate);
  }
  getStaffCode(shopCode: ShopCodeModel): Observable<any> {
    return this.http.post<any>(config.search_VDS_Staff_GetStaff_API, shopCode);
  }

  cationConfigDeclareVDS(model: DeclareVDSModel) {
    return this.http.post(config.config_action_VDS_API, model);
  }

  getAllUnit(): Observable<UnitModel[]> {
    return this.http.get<UnitModel[]>(config.allUnit_VDS_API);
  }

  reloadTree(reload: boolean) {
    this.reloadTreeAdd.next(reload);
  }
}
