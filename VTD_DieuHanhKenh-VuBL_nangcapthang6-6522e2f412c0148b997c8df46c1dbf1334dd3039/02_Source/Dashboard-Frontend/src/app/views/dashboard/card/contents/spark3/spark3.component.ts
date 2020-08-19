import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-spark3',
  templateUrl: './spark3.component.html',
  styleUrls: ['./spark3.component.scss']
})
export class Spark3Component implements OnInit {

  @Input() content: any;
  @Output() detailSpark3 = new EventEmitter();

  constructor() { }

  lstData: any[];

  ngOnInit() {
    this.lstData = this.content['lstData'];
  }

  lv2Spark3(i: number, month) {
    const emitObject = {pRow: i, month: month};
    this.detailSpark3.emit(emitObject);
  }
}
