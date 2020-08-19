import { AfterViewInit, Component, EventEmitter, HostListener, Input, OnChanges, OnInit, Output, ViewChild } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { DrilldownModel } from '../../../models/Drilldown.model';
import { DashboardModel } from '../../../models/dashboard.model';
import { config } from '../../../config/application.config';
import { HttpClient } from '@angular/common/http';
import { ContentsComponent } from './contents/contents.component';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'card-dashboard',
  templateUrl: 'card.component.html',
  styleUrls: ['card.component.scss']
})

export class CardComponent implements OnInit, AfterViewInit, OnChanges {
  fo = {
    'height': '400px'
  };

  // @ts-ignore
  constructor(private translate: TranslateService,
    private http: HttpClient) {
  }

  // tslint:disable-next-line:no-input-rename
  @Input() isExcelCard: boolean;
  @Input('card-data') cardData2: any;
  @Input() indexCard: any;
  @Input() unitOrStaff: any;
  @Input() cardLength: number;
  @Input() drilldownLength: number;
  @Input() chartDetail: any;
  @Input() codeWarning: any;
  @Input() dashBoadModel: DashboardModel;
  @Output() getCardIdto = new EventEmitter();
  @Output() onclickCard = new EventEmitter();
  @Output() clickRow = new EventEmitter<DrilldownModel>();
  @Output() clickChart = new EventEmitter();
  @Output() clickTopRight = new EventEmitter();
  @Output() showDetail = new EventEmitter();
  @Output() showDetailSpark2 = new EventEmitter<DrilldownModel>();
  @Output() showDetailSpark3 = new EventEmitter<DrilldownModel>();
  @Output() changeCodeSpark2 = new EventEmitter<any>();

  isCollapseTable = true;
  isFullTable = true;
  isSpinner = true;
  showString;
  hideString;
  collapseString;
  cardId;
  contentData;

  expanseString;
  detail;
  showBar = false;
  drill;

  @HostListener('window:resize', ['$event'])

  ngOnInit(): void {
    this.contentData = null;
    this.detail = this.translate.instant('management.dashboard.detail');
    // this.detail = this.translate.instant('management.dashboard.show');
    this.showString = this.translate.instant('management.dashboard.show');
    this.hideString = this.translate.instant('management.dashboard.hide');
    this.collapseString = this.translate.instant('management.dashboard.collapse');
    this.expanseString = this.translate.instant('management.dashboard.expanse');
  }

  collapseTable() {
    this.isCollapseTable = !this.isCollapseTable;
    setTimeout(() => {
      window.dispatchEvent(new Event('resize'));
    }, 0);
  }

  zoomTrue() {
    if (this.cardData2['contents'].length == 1 &&
      (this.cardData2['contents'][0]['type'] == 'BAR_CHART' || this.cardData2['contents'][0]['type'] == 'COLUMN_CHART')) {
      this.dashBoadModel.expand = 1;
      this.http.post(config.allShop_API, this.dashBoadModel).subscribe(
        data => {
          this.contentData = data['data']['cards'][0]['contents'][0];
          // this.contentDblClick = data['data']['cards'][this.indexCard]['contents'][indexContent];
          // this.isDblClick = !this.isDblClick;
        },
        () => {
        },
        () => {
          this.isFullTable = false;
          this.showBar = true;
          this.resizeTb();
          (document.querySelectorAll('.table-responsive')[0] as HTMLElement).style.maxHeight = 'calc(100vh - 32.31px)';
        });
    } else {
      this.isFullTable = false;
      this.showBar = false;
      this.resizeTb();
    }
  }

  zoomFalse() {
    this.isFullTable = true;
    this.showBar = false;
    this.resizeTb();
    (document.querySelectorAll('.table-responsive')[0] as HTMLElement).style.maxHeight = '400px';
  }

  resizeTb() {

    // this.isFullTable = !this.isFullTable;
    this.isSpinner = false;
    if (this.isFullTable === false) {
      (document.querySelectorAll('.table-responsive')[0] as HTMLElement).style.maxHeight = 'calc(100vh - 32.31px)';
    } else {
      (document.querySelectorAll('.table-responsive')[0] as HTMLElement).style.maxHeight = '400px';
    }
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

  ngAfterViewInit(): void {
    // (document.querySelectorAll('.card-responsive')[0] as HTMLElement).style.setProperty('height', 'unset', 'important');
  }

  clickCard(value: string) {
    this.onclickCard.emit(value);
  }

  clickDetail(drilldownObject: DrilldownModel) {
    this.clickRow.emit(drilldownObject);
  }

  detailChart(serviceId: number, drilldownObject: number) {

    const drilldownModel = new DrilldownModel(serviceId, drilldownObject, null);
    this.clickChart.emit(drilldownModel);
  }

  onClickTop(value: any) {
    this.clickTopRight.emit(value);
  }

  lv2Spark2() {
    let drilldownObject;
    if (this.cardData2['contents'].length === 1 && this.cardData2['contents'][0]['type'] === 'CONTINUITY_FAIL') {
      drilldownObject = this.cardData2['contents'][0]['drilldownObject'];
    }
    const drilldown: DrilldownModel = new DrilldownModel(null, drilldownObject,
      null, null, null, this.codeWarning);
    this.showDetailSpark2.emit(drilldown);
  }

  changeCodeWarning(drilldownModel: DrilldownModel) {
    this.codeWarning = drilldownModel.codeWarning;
    this.changeCodeSpark2.emit(drilldownModel.codeWarning);
    // this.showDetailSpark2.emit(drilldownModel);
    // console.log('aaaaaaaaaaaaa', this.codeWarning);
  }

  detailSpark3(drilldownModel: DrilldownModel) {
    this.showDetailSpark3.emit(drilldownModel);
  }

  ngOnChanges(): void {
  }
}
