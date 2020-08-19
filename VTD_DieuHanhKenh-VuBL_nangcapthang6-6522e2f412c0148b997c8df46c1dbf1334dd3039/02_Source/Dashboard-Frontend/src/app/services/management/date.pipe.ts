import {Injectable, Pipe, PipeTransform} from '@angular/core';
import {absFloor} from 'ngx-bootstrap/chronos/utils';
import {el} from '@angular/platform-browser/testing/src/browser_util';

@Pipe({
  name: 'dateFormat'
})

@Injectable()
export class DatePipe implements PipeTransform {
  transform(dateNumber: number) {
    const year = Math.floor(dateNumber / 10000);
    const month = Math.floor((dateNumber % 10000) / 100);
    const day = Math.floor(dateNumber % 100);
    let result: string;
    let monthStr: string;
    let dayStr: string;
    if (month < 10) {
      monthStr = '0' + month;
    } else {
      monthStr = '' + month;
    }

    if (day < 10) {
      dayStr = '0' + day;
    } else {
      dayStr = '' + day;
    }

    result = dayStr + '/' + monthStr + '/' + year;

    return result;
  }

}
