import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { config } from '../../config/application.config';
import { SearchReceive, WarningReceive } from '../../models/Warning-Receive';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WarningReceiveService {
  warningReceives: BehaviorSubject<Array<WarningReceive>> = new BehaviorSubject<Array<WarningReceive>>([]);
  warningReceives$: Observable<Array<WarningReceive>> = this.warningReceives.asObservable();
  reload: BehaviorSubject<Array<WarningReceive>> = new BehaviorSubject<Array<WarningReceive>>([]);
  reload$: Observable<Array<WarningReceive>> = this.reload.asObservable();
  reloadWarning: BehaviorSubject<number> = new BehaviorSubject<number>(0);
  reloadWarning$: Observable<number> = this.reloadWarning.asObservable();
  constructor(
    private http: HttpClient
  ) { }
  setListWarningReceives(warningReceive: WarningReceive[]) {
    this.warningReceives.next(warningReceive);
  }

  
  setReload(reload: WarningReceive[]) {
    this.reload.next(reload);
  }

  setReloadWarning(reload: number) {
    this.reloadWarning.next(reload);
  }

  getAll() {
    return this.http.get<any>(config.warningreceive_getAll_API);
  }

  getAllPartner() {
    return this.http.get<any>(config.partner_getAll_API);
  }

  getByCondition(searchReceive: SearchReceive, p: number, pageSize: number) {
    return this.http.post<any>(config.warningreceive_Search_API + "/" + p + "/" + pageSize, searchReceive);
  }

  SaveWarningReceive(warningReceive: WarningReceive) {
    if (warningReceive.mlngId === -1) {
      return this.http.post<any>(config.warningreceive_add_API, warningReceive);
    } else {
      return this.http.post<any>(config.warningreceive_update_API, warningReceive);
    }
  }

  DownloadTemplate() {
    let headers = new HttpHeaders();
    headers = headers.append('Content-Type', 'application/json');
    headers = headers.append('Access-Control-Allow-Headers', '*');
    return this.http.get(config.warningreceive_template_API, {
      headers: headers,
      observe: 'response',
      responseType: 'arraybuffer'
    });
  }

  Unlock(varrId: string[]) {
    return this.http.post<any>(config.warningreceive_unlock_API, varrId);
  }

  Lock(varrId: string[]) {
    return this.http.post<any>(config.warningreceive_lock_API, varrId);
  }

}
