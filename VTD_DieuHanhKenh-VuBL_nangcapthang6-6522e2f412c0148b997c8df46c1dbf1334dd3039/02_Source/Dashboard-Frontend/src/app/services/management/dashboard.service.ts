import {Injectable} from '@angular/core';
import {PlanmonthlyModel} from '../../models/Planmonthly.model';
import {config} from '../../config/application.config';
import {DashboardModel} from '../../models/dashboard.model';
import {HttpClient} from '@angular/common/http';

@Injectable()
export class DashboardService {

  constructor(private http: HttpClient) {
  }

  getDashboard(model: DashboardModel) {
    return this.http.post<any>(config.dashboard_API, model);
  }

  getDataTableDownload(model: DashboardModel) {
    return this.http.post<any>(config.downloadXLSX_Table_API, model);
  }
}
