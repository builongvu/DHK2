import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { DrilldownModel } from '../../../../models/Drilldown.model';
import { DashboardModel } from '../../../../models/dashboard.model';
import { config } from '../../../../config/application.config';
import { HttpClient } from '@angular/common/http';

export class ChartDetail {
  groupId: number;
  chartId: number;
  isZoom: boolean;

  constructor(groupId: number, chartId: number, isZoom: boolean) {
    this.groupId = groupId;
    this.chartId = chartId;
    this.isZoom = isZoom;
  }
}

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'contents-dashboard',
  templateUrl: 'contents.component.html',
  styleUrls: ['contents.component.scss'],
})

export class ContentsComponent implements OnInit, AfterViewInit {
  // tslint:disable-next-line:no-input-rename
  @Input('contents') contents: any;
  @Input() titleCard: any;
  @Input() isExcelContent: boolean;
  @Input() indexCard: any;
  @Input() cardLength: number;
  @Input() drilldownLength: number;
  @Input() barData: any;
  @Input() isZoomCard: boolean;
  @Input() codeWarning: boolean;
  @Input() chartDetails: ChartDetail[];
  @Input('dashboardModel') dashModel: DashboardModel;
  @Output() clickContents = new EventEmitter();
  @Output() clickDetail = new EventEmitter<DrilldownModel>();
  @Output() zoomChart: EventEmitter<boolean> = new EventEmitter();
  @Output() clickTopRight = new EventEmitter();
  @Output() showDetail = new EventEmitter();
  @Output() changeCode = new EventEmitter<DrilldownModel>();
  @Output() lv2Spark3 = new EventEmitter<DrilldownModel>();
  isZoom: boolean;
  zoomColum = false;
  zoomCharts: boolean[] = [];

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {
  }


  herStyle = {
    'height': '100%'
  };
  myStyle = {
    'position': 'fixed',
    'top': '0',
    'right': '0',
    'background': 'white',
    'height': '100vh',
    'width': '100%',
    'z-index': '5000'
  };
  my2Style = {
    'position': 'fixed',
    'top': '0',
    'right': '0',
    'background': 'white',
    'height': '100vh',
    'width': '100%',
    'z-index': '5000'
  };
  my3Style = {
    'position': 'fixed',
    'top': '0',
    'right': '0',
    'background': 'white',
    'height': '100vh',
    'width': '100%',
    'z-index': '5000'
  };
  isSpinner = true;
  isDblClick = false;
  contentDblClick: any;
  ngOnInit(): void {
  }


  clickZoom() {
    this.isZoom = true;
    this.zoomChart.emit(this.isZoom);
  }

  clickTop(value: string) {
    // console.log('click top');
    this.clickContents.emit(value);
  }

  clickService(drilldownObject) {
    this.clickDetail.emit(drilldownObject);
  }

  onClickTop(event: any) {
    this.clickTopRight.emit(event);
  }

  zoom(chartId?: number) {
    this.zoomColum = !this.zoomColum;
    this.isSpinner = false;

    setTimeout(() => {
      window.dispatchEvent(new Event('resize'));
    }, 300);

    setTimeout(() => {
      window.dispatchEvent(new Event('resize'));
    }, 500);

    setTimeout(() => {
      this.isSpinner = !this.isSpinner;
    }, 800);
  }

  zoomDetail(indexContent: number, chartId?: number) {
    this.dashModel.expand = 1;
    this.http.post(config.allShop_API, this.dashModel).subscribe(
      data => {
        // console.log('sssssss', data['data']['cards'][this.indexCard]['contents'][indexContent]);
        this.contentDblClick = data['data']['cards'][this.indexCard]['contents'][indexContent];
        this.isDblClick = !this.isDblClick;
      },
      () => {
      },
      () => {
        if (this.contents[indexContent]['type'] == 'BAR_CHART') {
          this.zoom();
        } else if (this.contents[indexContent]['type'] == 'COLUMN_CHART') {
          this.zoom2(chartId);
        }
      }
    );
  }

  ngAfterViewInit(): void {
    // console.log('chartdetails: ', this.chartDetails);
    // console.log('bar: ', this.barData);
  }

  zoom2(chartId: number) {
    this.chartDetails.forEach(
      chartdetail => {
        if (chartdetail.chartId === chartId) {
          chartdetail.isZoom = !chartdetail.isZoom;
          // console.log(chartdetail.isZoom);
          this.isSpinner = false;
          const body = document.getElementsByTagName('body');
          if (chartdetail.isZoom) {
            body[0].style.overflow = 'hidden';
          } else {
            body[0].style.overflow = 'auto';
          }
        }
      }
    );
    // this.zoomColum2 = !this.zoomColum2;

    setTimeout(() => {
      window.dispatchEvent(new Event('resize'));
    }, 500);

    setTimeout(() => {
      this.isSpinner = !this.isSpinner;
    }, 500);
  }

  changeCodeSpark2(code: any, drilldown: number) {
    const drillModel: DrilldownModel = new DrilldownModel(null, drilldown);
    drillModel.codeWarning = code;
    this.changeCode.emit(drillModel);
  }

  detailSpark3(emitObject: any, groupId: any) {
    const drillModel: DrilldownModel = new DrilldownModel(null, null, null);
    drillModel.drilldownObject = groupId;
    drillModel.index = emitObject.pRow;
    drillModel.month = emitObject.month;
    this.lv2Spark3.emit(drillModel);
  }
}
