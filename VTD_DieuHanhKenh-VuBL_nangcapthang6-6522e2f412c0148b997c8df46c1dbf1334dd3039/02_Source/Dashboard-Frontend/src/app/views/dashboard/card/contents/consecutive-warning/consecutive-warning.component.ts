import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {config} from '../../../../../config/application.config';
import {HttpClient} from '@angular/common/http';
import {DashboardModel} from '../../../../../models/dashboard.model';

@Component({
  selector: 'app-consecutive-warning',
  templateUrl: './consecutive-warning.component.html',
  styleUrls: ['./consecutive-warning.component.scss']
})
export class ConsecutiveWarningComponent implements OnInit {

  @Input() content: any;
  @Input() dashModel: DashboardModel;
  @Output() changeCodeSpark2 = new EventEmitter();

  constructor(private http: HttpClient) { }

  dataSelect: any[] = [];
  codeWarning;

  ngOnInit() {
    this.getDataSelect();

  }
  getDataSelect() {
    this.http.get(config.apparam_getbytype_API + '/CONSECUTIVE_WARNING').subscribe(
      data => {
        this.dataSelect = data['data'];
        this.codeWarning = this.dashModel.codeWarning ? this.dashModel.codeWarning : data['data'][0]['code'];
      }
    );
  }

  changeCode() {
    this.changeCodeSpark2.emit(this.codeWarning);
  }
}
