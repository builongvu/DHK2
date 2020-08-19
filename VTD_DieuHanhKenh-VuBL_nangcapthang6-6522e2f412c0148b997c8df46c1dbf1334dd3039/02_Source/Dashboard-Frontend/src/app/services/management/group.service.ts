import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Injectable } from '@angular/core';

import { config } from '../../config/application.config';
import { GroupModel, Searchgroup } from '../../models/group.model';
import { TargetGroupSearchModel } from '../../models/TargetGroupSearchModel';

@Injectable()
export class GroupsService {

  constructor(private http: HttpClient) {
  }

  getGroupActive() {
    return this.http.get<any>(config.getGroupByStatus);
  }


  getGroups(count = -1): Observable<GroupModel[]> {
    return this.http.get<GroupModel[]>(config.group_API).pipe(
      map(response => response['data'].filter((post, i) => i > count))
    );
  }

  getGroupByStatus() {
    return this.http.get<GroupModel[]>(config.group_API);
  }

  getGroupById(id: number): Observable<GroupModel[]> {
    return this.http.get<GroupModel[]>(`${config.productId_API}/${id}`);
    // return this.http.get<GroupProduct[]>(`${config.product_group_API}?page=${id}`);
  }
  insertData(data: GroupModel): Observable<GroupModel> {
    return this.http.post<GroupModel>(`${config.insertGroup_API}`, data);
  }

  updateData(data: GroupModel): Observable<GroupModel> {
    return this.http.put<GroupModel>(`${config.updateGroup_API}`, data);
  }
  getDatas(targetGroupSearch: TargetGroupSearchModel) {
    return this.http.post<GroupModel[]>(config.targetGroup_API, targetGroupSearch);
  }
  // getDatas(targetGroupSearch: TargetGroupSearchModel){
  //   return this.http.get<{GroupModel[]>(config.targetGroup_API);
  // }
  getAllGroup(count = -1): Observable<GroupModel[]> {
    return this.http.get<GroupModel[]>(config.getAllGroup_APi).pipe(
      map(response => response['data'].filter((post, i) => i > count))
    );
  }
  downloadGroupTemplate() {
    let headers = new HttpHeaders();
    headers = headers.append('Content-Type', 'application/json');
    headers = headers.append('Access-Control-Allow-Headers', '*');
    return this.http.get(config.downloadGroupTemplate_API, {
      headers: headers,
      observe: 'response',
      responseType: 'arraybuffer'
    });
  }

  getAllGroupService() {
    return this.http.get(config.getAllGroup_API);
  }


  lock(varrId: string[]) {
    return this.http.post<any>(config.lockGroup_API, varrId);
  }
  unlock(varrId: string[]) {
    return this.http.post<any>(config.unlockGroup_API, varrId);
  }
  getSearch(searchgroup: GroupModel, p: number, pagesize: number) {
    return this.http.post<GroupModel>(config.getSearchGroupService + '/' + p + '/' + pagesize, searchgroup);
  }
}
