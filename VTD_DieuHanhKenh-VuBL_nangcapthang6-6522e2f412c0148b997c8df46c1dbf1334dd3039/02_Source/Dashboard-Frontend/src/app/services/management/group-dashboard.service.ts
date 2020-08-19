import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {config} from '../../config/application.config';
import {GroupDashboardModel} from '../../models/group-dashboard.model';

@Injectable()
export class GroupDashboardService {

  constructor(private http: HttpClient) {
  }

  getGroupsDash() {
    return this.http.get<GroupDashboardModel[]>(config.group_dashboard_API);
  }
}
