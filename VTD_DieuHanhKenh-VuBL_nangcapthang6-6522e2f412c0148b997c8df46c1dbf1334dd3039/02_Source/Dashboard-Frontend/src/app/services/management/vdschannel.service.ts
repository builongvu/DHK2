import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { config } from '../../config/application.config';

@Injectable({
  providedIn: 'root'
})
export class VdschannelService {

  constructor(
    private http: HttpClient
  ) { }
  
  getAllChannel(){
    return this.http.get<any>(config.getAllVdsChannel);
  }
}
