import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {config} from '../../config/application.config';
import {ItemTreeModel} from '../../models/ItemTree.model';

@Injectable({
  providedIn: 'root'
})
export class UnittreeService {

  constructor(private http: HttpClient) {
  }

  getAllUnitTree(): Observable<ItemTreeModel[]> {
    return this.http.get<ItemTreeModel[]>(config.unitTree_API);
  }
}
