import {AfterViewInit, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import * as Highcharts from 'highcharts';
import {UUID} from 'angular2-uuid';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'column-chart',
  templateUrl: 'column-chart.component.html',
  styleUrls: ['column-chart.component.scss']
})

export class ColumnChartComponent implements OnInit, AfterViewInit {

  @Input() columnData: any;
  @Input() isDblClick: boolean;
  @Output() showDetailColumn = new EventEmitter();

  constructor() {
  }

  idShow = false;

  optionBarChart: any = {
    chart: {
      type: 'column'
    },
    exporting: {
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
    title: {},
    subtitle: {},
    xAxis: {},
    yAxis: {
      min: 0,
      title: {
        text: 'Population (millions)',
        align: 'middle'
      },
      labels: {
        overflow: 'justify'
      }
    },
    tooltip: {},
    plotOptions: {
      bar: {
        dataLabels: {
          enabled: false
        }
      },
      series: {
        dataLabels: {
          enabled: false
        }
      }
    },
    legend: {
      enabled: false,
      layout: 'vertical',
      align: 'right',
      verticalAlign: 'top',
      x: -40,
      y: 80,
      floating: true,
      borderWidth: 1,
      backgroundColor:
        Highcharts.defaultOptions.legend.backgroundColor || '#FFFFFF',
      shadow: true
    },
    credits: {
      enabled: false
    },
    series: []
  };
  chartId: any = UUID.UUID();
  noDataD: boolean;

  ngAfterViewInit(): void {
    if (this.columnData) {
      setTimeout(() => {
        if (this.columnData['noData']) {
          return this.noDataD = true;
        }
      }, 100);
      let points: any[] = [];
      // tslint:disable-next-line:prefer-const
      let tempData: any[] = [];
      const categories = this.columnData['categories'];
      const title = this.columnData['title'];
      let unitType;
      // this.optionBarChart.yAxis.title.text = 'luong';
      const series = this.columnData['series'];
      series.forEach(
        sery => {
          unitType = sery['unitType'];
          const name = sery['title'];
          // tslint:disable-next-line:prefer-const
          let values: any[] = [];
          const viewValues: any[] = [];
          const color = sery['color'];
          points = sery['points'];
          points.forEach(
            point => {
              values.push({y: point['value'], view: point['viewValue']});
              viewValues.push(point['viewValue']);
            }
          );
          tempData.push({data: values, name: 'name', options: {viewValue: viewValues}, color: color});
        }
      );
      this.optionBarChart.series = tempData;
      this.optionBarChart.title.text = title;
      this.optionBarChart.xAxis.categories = categories;
      this.optionBarChart.yAxis.title.text = unitType;
      this.optionBarChart.yAxis.title.align = 'middle';
      this.optionBarChart.exporting.filename = title;
      this.optionBarChart.tooltip = {
        headerFormat: '<b>{point.key}</b>',
        backgroundColor: 'white',
        pointFormat: ': <b>{point.view}</b><br/>',
        // valueSuffix: ' ' + unitType,
        shared: true
      };

      const greetingPoster = new Promise((resolve, reject) => {
        resolve('chart is rendering....');
      });
      greetingPoster.then(res => Highcharts.chart('containercolumn' + this.chartId, this.optionBarChart));
    }

  }

  ngOnInit(): void {
    this.noDataD = false;
  }

  showDetail() {
    this.showDetailColumn.emit();
    this.idShow = !this.idShow;
  }

  // showDetail2() {
  //
  // }
}
