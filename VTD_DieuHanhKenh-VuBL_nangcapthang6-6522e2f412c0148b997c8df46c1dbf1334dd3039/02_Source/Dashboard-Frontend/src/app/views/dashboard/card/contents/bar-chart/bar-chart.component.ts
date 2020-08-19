import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import * as Highcharts from 'highcharts';
import {UUID} from 'angular2-uuid';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'bar-chart',
  templateUrl: 'bar-chart.component.html',
  styleUrls: ['bar-chart.component.scss']
})

export class BarChartComponent implements OnInit, AfterViewInit {

  @Input() barData: any;
  constructor() {
  }

  optionBarChart: any = {
    chart: {
      type: 'bar'
    },
    exporting: {
      tableDecimalPoint: ',',
      tableDecimalValue: 2,
      csv: {
        columnHeaderFormatter: function(item, key) {
          if (item.name) {
            return 'Giá trị';
          }
          return 'Đơn vị';
        }
      },
      buttons: {
        contextButton: {
          menuItems: ['downloadPNG', 'downloadJPEG', 'downloadXLS']
        }
      },
      enabled: true,
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
        align: 'low'
      },
      labels: {
        overflow: 'justify'
      }
    },
    tooltip: {},
    plotOptions: {
      bar: {
        dataLabels: {
          enabled: true
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
    if (this.barData) {
      setTimeout(() => {
        if (this.barData['noData']) {
          return this.noDataD = true;
        }
      }, 100);
      let points: any[] = [];
      // tslint:disable-next-line:prefer-const
      let tempData: any[] = [];
      const categories = this.barData['categories'];
      const title = this.barData['title'];
      let unitType;
      // this.optionBarChart.yAxis.title.text = 'luong';
      const series = this.barData['series'];
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
              values.push({y: point['value'], view: point['viewValue'], color: point['color']});
              viewValues.push(point['viewValue']);
            }
          );
          tempData.push({data: values, name: 'name', options: { viewValue: viewValues}, color: color});
        }
      );
      this.optionBarChart.series = tempData;
      this.optionBarChart.title.text = title;
      this.optionBarChart.xAxis.categories = categories;
      this.optionBarChart.yAxis.title.text = unitType;
      this.optionBarChart.yAxis.title.align = 'high';
      this.optionBarChart.exporting.filename = title;
      // this.optionBarChart.exporting.chartOptions.title.style.fontSize = '14px';
      this.optionBarChart.tooltip = {
        backgroundColor: 'white',
        headerFormat: '<b>{point.key}</b>',
        pointFormat: ': <b>{point.view}</b><br/>',
          valueSuffix: ' ' + unitType,
          shared: true
      };

      const greetingPoster = new Promise((resolve, reject) => {
        resolve('chart is rendering....');
      });
      greetingPoster.then(res => Highcharts.chart('container' + this.chartId, this.optionBarChart));
    }

  }

  ngOnInit(): void {
    this.noDataD = false;
  }

}
