import { TranslateService } from '@ngx-translate/core';
import { LogService } from './../../../../models/LogService.model';
import { ServiceService } from './../../../../services/management/service.service';
import { Component, OnInit, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'app-target-history',
  templateUrl: './target-history.component.html',
  styleUrls: ['./target-history.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class TargetHistoryComponent implements OnInit {

  arrLogOfService: any[] = [];
  logService: LogService[] = [];
  serviceName: string;
  serviceID: number;
  service: any;
  recordInPage: number;
  recordInPageMin: number;
  p;

  constructor(private serviceService: ServiceService, private translate: TranslateService) {
  }

  ngOnInit() {
    this.serviceService.service$.subscribe((data: any) => {
      this.logService = [];
      this.service = data;
      this.logService = [];
      if (data.length !== 0) {
        if (data.id !== 0 && data.id !== -1) {
          this.serviceService.getLogOfServiceByServiceId((data.id)).subscribe((log: any) => {
            if (log.data.length > 0) {
              this.logService = log.data;
              this.convertNameColumn(this.logService);
            }
            if (log.data != null) {
              this.p = 1;
              if (log.data.length > 10) {
                this.recordInPage = 10;
                this.recordInPageMin = this.recordInPage - (this.recordInPage - 1);
              } else {
                this.recordInPage = log.data.length;
                this.recordInPageMin = 1;
              }
            }
          });

        }
      }
    });
  }

  pageChanged(event) {
    this.p = event;

    if (this.logService.length > 10) {
      this.recordInPage = 10;
      this.recordInPage = 10 * this.p;
      this.recordInPageMin = this.recordInPage - 9;

      if (this.recordInPage > this.logService.length) {
        this.recordInPage = this.logService.length;
      }

    } else {
      this.recordInPage = this.logService.length;
    }
  }

  convertNameColumn(logSerive: LogService[]) {
    logSerive.forEach(item => {
      if (item.actionCode === '01') {
        if (item.objectName == 'Service') {
          item.columnName = this.translate.instant(item.columnName) + this.translate.instant('management.service.history.service');
        }
      } else {
        if (item.objectName == 'Service') {
          item.columnName = this.translate.instant('management.service.columnName.service');
        }
        item.newValue = '';
      }

    });
  }
}
