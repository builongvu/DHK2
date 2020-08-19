import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { config } from '../../config/application.config';
import { MappingGroupChannel } from '../../models/mappinggroupchannel.model';

@Injectable({
  providedIn: 'root'
})
export class MappinggroupchannelService {

  constructor(
    private http: HttpClient
  ) { }


  getByCondition(mappingGroup: MappingGroupChannel) {
    if (mappingGroup.status == '-1') {
      mappingGroup.status = null;
    }
    return this.http.post<any>(`${config.apiUrl}/mappingGroupChannel/getByCondition`, mappingGroup);
  }

  addMapping(mapping: MappingGroupChannel) {
    if(mapping.id != null){
      return this.http.post<any>(`${config.apiUrl}/mappingGroupChannel/updateMappingGroupChannel`, mapping);
    }else{
      return this.http.post<any>(`${config.apiUrl}/mappingGroupChannel/addMappingGroupChannel`, mapping);
    }
  }

  getById(id: number) {
    return this.http.get<any>(`${config.apiUrl}/mappingGroupChannel/getById/${id}`);
  }
}
