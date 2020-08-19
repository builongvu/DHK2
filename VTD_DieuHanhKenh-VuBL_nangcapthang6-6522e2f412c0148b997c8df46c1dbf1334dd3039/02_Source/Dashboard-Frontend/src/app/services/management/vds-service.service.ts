import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { config } from '../../config/application.config';

@Injectable({
  providedIn: 'root'
})
export class VdsService {

  constructor(
    private http: HttpClient
  ) { }
  
  getActiveVDSGroup(){
    return this.http.get<any>(config.getActiveVDSGroupC_API);
  }
  
  addVDSGroup(vdsGroup: any){
    return this.http.post<any>(config.addVDSGroupC_API, vdsGroup);
  }

  getAllVdsChannel() {
    return this.http.get<any>(`${config.apiUrl}/management/channel/getAll`);
  }
}

