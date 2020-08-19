import { config } from './../../config/application.config';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class PositionService {

  constructor(private http: HttpClient) { }

  getAllPosition() {
    return this.http.get<any>(config.getAllPosition_API);
  }
}
