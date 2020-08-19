import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { config } from '../../config/application.config';
import { map } from 'rxjs/operators';
import { warningservice } from '../../models/warningservice';
import { ServiceWarningModel } from '../../models/ServiceWarning.model';



@Injectable({ providedIn: 'root' })
export class warningconfig {
  constructor(private http: HttpClient) {
  }

  serviceWarning: BehaviorSubject<any> = new BehaviorSubject<any>([]);
  serviceWarning$: Observable<any> = this.serviceWarning.asObservable();
  reloadWarning: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  reloadWarning$: Observable<boolean> = this.reloadWarning.asObservable();

  // serviceTree: BehaviorSubject<any> = new BehaviorSubject<any>([]);
  // serviceTree$: Observable<any> = this.serviceTree.asObservable();

  setServiceWarning(service: any) {
    this.serviceWarning.next(service);
  }

  setReloadWarning(reload: boolean) {
    this.reloadWarning.next(reload);
  }

  // setServiceTree(serviceTree: any) {
  //   this.serviceTree.next(serviceTree);
  // }
  // getAllWarning(): Observable<warningconfig[]>{
  //     return this.http.get<warningconfig[]>(config.getAllWarning_API)
  // }
  getAllWarning(): Observable<any> {
    return this.http.get<any>(config.getAllWarning_API)
    //  map(response => response['data'].filter((post, i) => i > count))
  }
  getAllWarningById(idWarning: number): Observable<any> {
    return this.http.get<any>(config.warning_getAllWarningById_API + '/' + idWarning);
  }
  downloadWarningTemplate() {
    let headers = new HttpHeaders();
    headers = headers.append('Content-Type', 'application/json');
    headers = headers.append('Access-Control-Allow-Headers', '*');
    return this.http.get(config.downloadWarnningTemplate_API, {
      headers: headers,
      observe: 'response',
      responseType: 'arraybuffer'
    });
  }
}
