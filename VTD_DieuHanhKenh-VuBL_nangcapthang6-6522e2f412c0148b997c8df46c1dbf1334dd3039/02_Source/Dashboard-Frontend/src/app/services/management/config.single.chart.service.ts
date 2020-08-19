import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {config} from '../../config/application.config';
import {Observable} from 'rxjs';
import {ConfigSingleCardModel} from '../../models/config.single.card.model';
import {ConfigSingleChartModel} from '../../models/ConfigSingleChart.model';

@Injectable({
  providedIn: 'root'
})
export class ConfigSingleChartServiceS {

  constructor(private http: HttpClient) {
  }

  getChartTypeAPI(apType: string) {
    return this.http.get(config.apparam_getbytype_API + '/' + apType);
  }

  getSingleChartByCondition(singleChart: ConfigSingleChartModel) {
    return this.http.post<ConfigSingleChartModel[]>(config.getSingleChartByCondition_API, singleChart);
  }

  addChart(singleChart: ConfigSingleChartModel) {
    return this.http.post(config.addSingleChart_API, singleChart);
  }

  deleteSingChart(chart: ConfigSingleChartModel) {
    return this.http.post(config.deleteSingleChart_API, chart);
  }

  updateSingleChart(chart: ConfigSingleChartModel) {
    return this.http.post(config.updateSingleChart_API, chart);
  }

}
