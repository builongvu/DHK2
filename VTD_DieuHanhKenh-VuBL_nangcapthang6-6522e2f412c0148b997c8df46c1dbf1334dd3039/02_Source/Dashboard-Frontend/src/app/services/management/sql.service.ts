import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {Injectable} from '@angular/core';

import {config} from '../../config/application.config';
import {GroupModel} from '../../models/group.model';
import {  SqlQueryModel } from '../../models/SqlQuery.model';

@Injectable()
export class SqlQueryService {

  constructor(private http: HttpClient) {
  }

  getSqlQuerys(count=-1): Observable<SqlQueryModel[]>{
    return this.http.get<GroupModel[]>(config.sqlQuery_API).pipe(
      map(response => response['data'].filter((post, i) => i > count))
    );
  }
}
