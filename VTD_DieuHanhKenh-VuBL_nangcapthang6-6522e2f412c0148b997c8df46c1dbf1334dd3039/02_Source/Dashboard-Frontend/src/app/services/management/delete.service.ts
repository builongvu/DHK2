import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';

import {config} from '../../config/application.config';
import {PlanmonthlyModel} from '../../models/Planmonthly.model';

@Injectable()
export class DeleteService {

  constructor(private http: HttpClient) {
  }

  deleteMonthRecords(data: PlanmonthlyModel[]) {
    return this.http.post(config.deleteMonthly_API, data);
  }

  deleteQuarterRecords(data: PlanmonthlyModel[]) {
    return this.http.post(config.deleteQuarterly_API, data);
  }

  deleteYearRecords(data: PlanmonthlyModel[]) {
    return this.http.post(config.deleteYearly_API , data);
  }

}
