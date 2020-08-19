import { Injectable } from '@angular/core';

@Injectable()
export class I18n {
  languages = [
    {
      'key': 'vi'
    },
    {
      'key': 'en'
    }];
  language = 'vi';
}
