import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Injectable} from '@angular/core';

import {config} from '../../config/application.config';
import {SearchModel} from '../../models/Search.model';
import {PlanmonthlyModel} from '../../models/Planmonthly.model';

@Injectable()
export class PlanmonthlyService {

  constructor(private http: HttpClient) {
  }

  getDatas(search: SearchModel) {
    return this.http.post<PlanmonthlyModel[]>(config.plan_monthly_API, search);
  }

  updateMonthFschedule(data: PlanmonthlyModel[]) {
    return this.http.put(config.updateMonthly_API, data);
  }

  updateQuarterFschedule(data: PlanmonthlyModel[]) {
    return this.http.put(config.updateQuarterly_API, data);
  }
  updateYearFschedule(data: PlanmonthlyModel[]) {
    return this.http.put(config.updateYearly_API, data);
  }

}
