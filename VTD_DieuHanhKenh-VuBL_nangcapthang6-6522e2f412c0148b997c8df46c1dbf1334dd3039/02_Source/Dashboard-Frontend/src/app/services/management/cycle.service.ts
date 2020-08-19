import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {config} from '../../config/application.config';

@Injectable()
export class CycleService {
  constructor(private http: HttpClient) {
  }

  getCycles() {
    return this.http.get<any>(config.cycle_VDS_API);
  }
}
