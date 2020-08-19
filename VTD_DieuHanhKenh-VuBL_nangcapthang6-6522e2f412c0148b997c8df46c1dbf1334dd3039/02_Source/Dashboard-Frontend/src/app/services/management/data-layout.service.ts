import {Injectable} from '@angular/core';
import {Subject} from 'rxjs';
import {DashboardModel} from '../../models/dashboard.model';

@Injectable()
export class DataLayoutService {
  // Observable string source
  private dataStringSource = new Subject<DashboardModel>();

  // Observable string stream
  dataString$ = this.dataStringSource.asObservable();

  // Service message commands
  insertData(data: any) {
    this.dataStringSource.next(data);
  }
}
