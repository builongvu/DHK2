import {AfterViewInit, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import * as Highcharts from 'highcharts';
import {UUID} from 'angular2-uuid';
import {config} from '../../../../../config/application.config';
import {TranslateService} from '@ngx-translate/core';

declare var require: any;
const Boost = require('highcharts/modules/boost');
const noData = require('highcharts/modules/no-data-to-display');
const More = require('highcharts/highcharts-more');
const ExportingModule = require('highcharts/modules/exporting');
const ExportingDataModule = require('highcharts/modules/export-data');

Boost(Highcharts);
noData(Highcharts);
More(Highcharts);
ExportingModule(Highcharts);
ExportingDataModule(Highcharts);

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'line-chart',
  templateUrl: 'linechart.component.html',
  styleUrls: ['linechart.component.scss']
})

export class LinechartComponent implements OnInit, AfterViewInit {
  // tslint:disable-next-line:no-input-rename
  @Input('chart-data') chartData: any;
  @Input() titleCard: any;
  @Input() contentLength: number;
  // tslint:disable-next-line:no-input-rename
  @Input('chart-datas') chartDatas: any[];
  // tslint:disable-next-line:no-input-rename
  @Input('isFull') isFull: boolean;
  @Input() isDblClick: boolean;
  @Output() zoomLine = new EventEmitter();
  detailMenu: string;
  public optionsTable: any = {
    chart: {
      type: 'line',
      height: '33px'
    },
    exporting: {
      enabled: false
    },
    plotOptions: {
      series: {
        enableMouseTracking: false,
        states: {
          hover: {
            enabled: false
          }
        }
      },
      line: {
        lineWidth: 0.8,
        marker: {
          enabled: true,
          radius: 0.8
        }
      }
    },
    title: {
      text: ''
    },
    credits: {
      enabled: false
    },
    tooltip: {},
    xAxis: {
      categories: []
    },
    yAxis: {
      visible: false
    }
  };


  public optionsChart: any = {
    legend: {
      itemStyle: {
        fontWeight: 400
      }
    },
    exporting: {
      sourceWidth: 800,
      sourceHeight: 500,
      useHTML: false,
      tableDecimalPoint: ',',
      tableDecimalValue: 2,
      csv: {
        columnHeaderFormatter: function (item, key) {
          if (item.name) {
            return item.name;
          }
          return '';
        }
      },
      buttons: {
        contextButton: {
          menuItems: ['downloadPNG', 'downloadJPEG', 'downloadXLS']
        }
      },
      filename: 'ngoc',
      chartOptions: {
        title: {
          style: {
            fontSize: '14px',
            textTransform: 'uppercase'
          }
        },
        filename: 'ngoc'
      }
    },
    lang: {
      noData: ''
    },
    chart: {
      type: 'line'
    },
    title: {},
    plotOptions: {
      series: {
        enableMouseTracking: true,
        states: {
          hover: {
            enabled: true
          }
        }
      },
      line: {
        lineWidth: 1,
        marker: {
          enabled: false,
          radius: 3,
          hover: {
            enabled: true
          }
        }
      }
    },
    credits: false,
    tooltip: {},
    xAxis: {
      labels: {
        // rotation: -50,
        // position: 'fixed'
      }
    },
    yAxis: {
      title: {}
    },
    series: {
      lineWidth: 1
    }
  };
  public optionsDefaultChart: any = {
    legend: {
      itemStyle: {
        fontWeight: 400
      }
    },
    lang: {
      noData: ''
    },
    chart: {
      type: 'line',
    },
    title: {
      text: ''
    },
    plotOptions: {
      series: {
        enableMouseTracking: true,
        states: {
          hover: {
            enabled: true
          }
        }
      },
      line: {
        lineWidth: 1,
        marker: {
          enabled: false,
          radius: 3,
          hover: {
            enabled: true
          }
        }
      }
    },
    credits: false,
    tooltip: {},
    xAxis: {
      labels: {
        // rotation: -50,
        // position: 'fixed'
      }
    },
    yAxis: {
      title: {}
    },
    series: {
      lineWidth: 1
    }
  };


  constructor(private translate: TranslateService) {
  }

  chartId: any = UUID.UUID();
  noDataD: boolean;
  isTable = true;

  ngOnInit() {
    this.noDataD = false;
    this.detailMenu = this.translate.instant('management.dashboard.menu');
  }

  ngAfterViewInit(): void {
    let a = true;
    if (this.chartData) {
      const tempArr = [];
      let dataChartArr: any[] = [];
      const viewArr: any[] = [];

      const seriesArr = this.chartData['series'];
      for (const b of seriesArr) {
        const points: any[] = [];
        const colorLine = b['color'];
        dataChartArr = b['points'];
        dataChartArr.forEach(
          point => {
            points.push(point['value']);
            viewArr.push(point['viewValue']);
          }
        );
        tempArr.push({name: b.title, data: points, showInLegend: false, color: colorLine});

      }
      this.optionsTable.series = tempArr;
      this.optionsTable.xAxis['categories'] = false;
      this.optionsTable.xAxis['visible'] = false;
      this.optionsTable.tooltip['enabled'] = true;

      Highcharts.chart('containerrow' + this.chartId, this.optionsTable);

    } else if (this.chartDatas) {
      // this.chartDatas.forEach(
      // data => {
      if (this.chartDatas) {
        setTimeout(() => {
          if (this.chartDatas['noData'] || this.chartDatas['categories'] == null) {
            a = false;
            return this.noDataD = true;
          }
        }, 100);
        const categories = this.chartDatas['categories'];
        const chartTitle = this.chartDatas['title'];
        const subTitle = this.chartDatas['subTitle'];
        let dataCharts: any[];
        let unitTypes: any;
        const tempCharts: any[] = [];
        const serriesChart = this.chartDatas['series'];
        if (serriesChart) {
          for (const serie of serriesChart) {
            const viewValues: any[] = [];
            let colors: any;
            const pointsChart: any[] = [];
            dataCharts = serie['points'];
            unitTypes = serie['unitType'];
            colors = serie['color'];
            let dashTypeValue = 'solid';
            let marker = true;
            if (serie['average']) {
              dashTypeValue = 'LongDash';
              marker = false;
            } else {
              marker = true;
            }

            dataCharts.forEach(
              point => {
                pointsChart.push({y: point['value'], view: point['viewValue'], mapSymbol: config.MAP_CHART_SYMBOL});
                viewValues.push(point['viewValue']);
              }
            );
            tempCharts.push({
              name: serie['title'], data: pointsChart, dashStyle: dashTypeValue, options: {viewValue: viewValues},
              marker: {enabled: marker}, color: colors
            });
            // pointsChart.for
          }
          this.optionsChart.series = tempCharts;
          this.optionsChart.xAxis['categories'] = categories;
          if (categories.length >= 15) {
          this.optionsChart.xAxis.labels = {rotation: -50};
          }
          // this.optionsChart.xAxis.categories.style = {
          //   display: 'fixed'
          // };
          this.optionsChart.xAxis['visible'] = true;
          this.optionsChart.yAxis['showEmpty'] = !a;
          this.optionsChart.lang.contextButtonTitle = this.detailMenu;
          this.optionsChart.yAxis.title.text = unitTypes;
          this.optionsChart.tooltip = {
            shared: true,
            useHTML: true,
            backgroundColor: 'white',
            headerFormat: '<small><b>{point.key}</b></small><table>',
            // tslint:disable-next-line:max-line-length
            pointFormat: '<tr><td ><span style="color: {series.color}">   {point.mapSymbol.circle}</span><span> {series.name}:</span></td>' +
              '<td><b>{point.view}</b></td></tr>',
            footerFormat: '</table>',
            // formatter : function() {
            // },
            valueDecimals: 2
          };

          this.optionsChart.title['text'] = chartTitle;
          if (chartTitle != '') {
            this.optionsChart.exporting.filename = chartTitle;
          } else {
            this.optionsChart.exporting.filename = this.titleCard;
            this.optionsChart.exporting.chartOptions.title.text = this.titleCard;
          }
          const greetingPoster = new Promise((resolve, reject) => {
            resolve('chart is rendering....');
          });
          greetingPoster.then(res => Highcharts.chart('containerchart' + this.chartId, this.optionsChart));
        } else {
          return;
        }
      } else {
        this.isTable = false;
      }
    } else {
      return;
    }

  }

  showDetail() {
    this.zoomLine.emit();
  }
}
