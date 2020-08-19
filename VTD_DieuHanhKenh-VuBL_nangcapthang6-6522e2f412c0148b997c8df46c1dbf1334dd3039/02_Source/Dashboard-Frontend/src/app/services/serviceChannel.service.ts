import { config } from './../config/application.config';
import { ServiceChannel } from './../models/ServiceChannel.model';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ServiceChannelService {

  constructor(private http: HttpClient) { }

  getServiceChannelByServiceId(idService: number): Observable<ServiceChannel[]> {
    return this.http.get<ServiceChannel[]>(config.serviceChannel_getByServiceId_API + '/' + idService);
  }
}
