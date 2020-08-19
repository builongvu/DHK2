import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {count, map} from 'rxjs/operators';

import {Observable} from 'rxjs';
import {config} from '../../config/application.config';
import {apParam} from '../../models/apParam.model';


@Injectable({providedIn: 'root'})
export class apparam {
  constructor(private http: HttpClient) {

  }

  getApparam(type: string) {
    return this.http.get<any>(config.apparam_API + '/' + type + '/1');
  }

  getAllApps(count = -1): Observable<apParam[]> {
    return this.http.get<apParam[]>(config.getAllApp_API).pipe(
      map(response => response['data'].filter((post, i) => i > count))
    );
  }
}
