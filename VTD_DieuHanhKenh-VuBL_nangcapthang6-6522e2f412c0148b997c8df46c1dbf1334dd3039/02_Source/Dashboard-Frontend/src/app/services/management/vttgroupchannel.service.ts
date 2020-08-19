import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { config } from '../../config/application.config';

@Injectable({
  providedIn: 'root'
})
export class VttgroupchannelService {

  constructor(private http: HttpClient) {
  }

  getActiveVttGroup() {
    return this.http.get<any>(config.getActiveVttChannel_API);
  }

  getAllNotMapping(code: string) {
    return this.http.get<any>(config.getAllNotMapping_API + '/' + code);
  }
}
