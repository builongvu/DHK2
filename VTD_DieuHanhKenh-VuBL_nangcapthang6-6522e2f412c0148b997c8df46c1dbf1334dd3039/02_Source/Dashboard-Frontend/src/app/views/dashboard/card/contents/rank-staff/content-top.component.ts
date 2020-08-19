import {AfterViewInit, Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {element} from 'protractor';
import {ConsecutiveWarningComponent} from '../consecutive-warning/consecutive-warning.component';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'content-top',
  templateUrl: 'content-top.component.html',
  styleUrls: ['content-top.component.scss']
})
export class ContentTopComponent implements OnInit {

  @Input() dataTop: any;

  @Output() onClickTop = new EventEmitter();

  constructor() {
  }

  listTop: any[] = [];
  noTopLeft = false;
  listData1: string;
  listData2: string;
  test = 'test';


  ngOnInit() {
    this.listData1 = null;
    this.listData2 = null;
    this.listTop = this.dataTop['listTop'];
    if (this.listTop[0]['data'].toString()) {
      this.listData1 = this.listTop[0]['data'].join(', ');
    } else {
      this.listData1 = '';
    }


    if (this.listTop[1]['data'].toString()) {
      this.listData2 = this.listTop[1]['data'].join(', ');
    } else {
      this.listData2 = '';
    }
  }

  clickTop() {
    this.onClickTop.emit(this.test);
  }
}
