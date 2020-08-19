import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { config } from '../../config/application.config';
import { SearchWarningSend, WarningSend } from '../../models/Warning-config';

@Injectable({
  providedIn: 'root'
})
export class WarningSendService {
  constructor(
    private http: HttpClient
  ) { }
  getAll() {
    return this.http.get<any>(config.warningsend_getAll_API);
  }
  getWarningLevel(type: string, status: string) {
    return this.http.get<any>(`${config.apparam_getbytypeandstatus_API}/${type}/${status}`); 
  }
  
  getWarningLevelNoStatus(type: string) {
    return this.http.get<any>(`${config.apparam_getbytypens_API}/${type}`); 
  }
  getAllService() {
    return this.http.get<any>(config.getAllService_API);
  }
  getByCondition(mobjSearch: SearchWarningSend, p: number, pageSize: number) {
    return this.http.post<any>(config.warningsend_Search_API + "/" + p + "/" + pageSize, mobjSearch);
  }
  saveWarningSend(newWarningSend: WarningSend) {
    if (newWarningSend.mlngId === -1) {
      return this.http.post<any>(config.warningsend_add_API, newWarningSend);
    } else {
      return this.http.post<any>(config.warningsend_update_API, newWarningSend);
    }
  }
  DownloadTemplate() {
    let headers = new HttpHeaders();
    headers = headers.append('Content-Type', 'application/json');
    headers = headers.append('Access-Control-Allow-Headers', '*');
    return this.http.get(config.warningsend_template_API, {
      headers: headers,
      observe: 'response',
      responseType: 'arraybuffer'
    });
  }
  DownloadWarningSend(filename: string) {
    let headers = new HttpHeaders();
    headers = headers.append('Content-Type', 'application/json');
    headers = headers.append('Access-Control-Allow-Headers', '*');
    return this.http.get(`${config.warningconfig_downloadResult_API}?filename=${filename}`, {
      headers: headers,
      observe: 'response',
      responseType: 'arraybuffer'
    });
  }

  Unlock(varrId: string[]) {
    return this.http.post<any>(config.warningsend_unlock_API, varrId);
  }

  Lock(varrId: string[]) {
    return this.http.post<any>(config.warningsend_lock_API, varrId);
  }
}
