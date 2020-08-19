import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DrilldownModel} from '../../../../../models/Drilldown.model';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'summary-top',
  templateUrl: 'sumary.component.html',
  styleUrls: ['sumary.component.scss']
})
export class SumaryComponent implements OnInit {

  @Input() summary: any;
  @Output() clickTop = new EventEmitter();

  constructor() {
  }

  noTopLeft = false;

  ngOnInit(): void {
    // for (let i = 0; i < this.summary['mlstSummary'].length; i++) {
    //   if (!this.summary['mlstSummary'][i]['viewPercent']) {
    //     this.noTopLeft = true;
    //     break;
    //   }
    // }

  }

  onClickTop(value: any, isTarget: boolean) {
    let isTargetNum: number;
    if (isTarget == true) {
      isTargetNum = 1;
    } else {
      isTargetNum = -1;
    }
    this.clickTop.emit(new DrilldownModel(null, value, null, null, isTargetNum));
  }
}
