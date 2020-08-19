import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { config } from '../../config/application.config';
import { ObjectConfig } from '../../models/objectconfig';

@Injectable({
  providedIn: 'root'
})
export class ConfigobjectService {

  constructor(
    private http: HttpClient
  ) { }

  getAll() {
    return this.http.get<any>(config.object_GetAll_API);
  }

  getAllNotDelete() {
    return this.http.get<any>(config.object_GetAllNotDelete_API);
  }

  getAllActive() {
    return this.http.get<any>(config.object_GetAllActive_API);
  }

  getAllByParentId(ParentId: number) {
    return this.http.get<any>(config.object_GetAllByParentId_API + ParentId);
  }

  getObjectByid(id: number) {
    return this.http.get<any>(config.object_GetById_API + id);
  }

  saveObject(newObject: ObjectConfig[]) {
    if (newObject[0].id === -1) {
      return this.http.post<any>(config.object_Save_API, newObject[0]);
    } else {
      return this.http.post<any>(config.object_Update_API, newObject);
    }
  }

  getImage(vstrUrl: string) {
    return this.http.post<any>(config.apiUrl + '/management/objectconfig/image', vstrUrl);
  }

  checkSelectParent(id: number, idSelect: number) {
    return this.http.get<any>(config.apiUrl + '/management/objectconfig/checkSelectParent/'+id+"/"+idSelect);
  }
}
