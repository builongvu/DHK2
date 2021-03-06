import { config } from './../../../../config/application.config';
import { Injectable } from '@angular/core';
import {TreeviewSelection} from 'ngx-treeview';
import { DefaultTreeviewI18n } from '../../vtt-target/default-treeview-i18n';
import { I18n } from '../../vtt-target/i18n';

@Injectable()
export class UnitTreeviewI18n extends DefaultTreeviewI18n {
  constructor(protected i18n: I18n) {
    super(i18n);
    console.log(i18n)
  }

  selections;



  getText(selection: TreeviewSelection): string {

    if (localStorage.getItem(config.user_default_language) === 'en') {
      this.i18n.language = 'en';
    } else {
      this.i18n.language = 'vi';
    }

    switch (selection.checkedItems.length) {
      case 0:
        return this.i18n.language === 'en' ? 'Select' : 'Chọn';
      case 1:
        return selection.checkedItems[0].text;
      default: {
        this.selections = '';
        for (let i = 0; i < selection.checkedItems.length; i++) {
          if (i === selection.checkedItems.length - 1) {
            this.selections += selection.checkedItems[i].text;
          } else {
            this.selections += selection.checkedItems[i].text + ', ';
          }
        }
        return this.selections;
      }
    }
  }

  getFilterNoItemsFoundText(): string {
    if (this.i18n.language === 'en') {
      return 'No datas found';
    } else {
      return 'Không có dữ liệu';
    }
  }

  getFilterPlaceholder(): string {
    if (this.i18n.language === 'en') {
      return 'Search';
    } else {
      return 'Tìm kiếm';
    }
  }
}
