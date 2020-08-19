import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { config } from '../../config/application.config';
import { apParam } from '../../models/apParam.model';

@Injectable({
  providedIn: 'root'
})
export class AssignOntimeService {

  constructor(
    private http: HttpClient
  ) { }

  getOntime() {
    return this.http.get<any>(`${config.apparam_getbytype_API}/ASSIGN_ONTIME`);
  }
  saveOntime(apParam: apParam) {
    if (apParam.id === -1) {
      return this.http.post<any>(`${config.apiUrl}/management/assignontime/addnewontime`, apParam);
    } else {
      return this.http.post<any>(`${config.apiUrl}/management/assignontime/updatenewontime`, apParam);
    }
  }
}
