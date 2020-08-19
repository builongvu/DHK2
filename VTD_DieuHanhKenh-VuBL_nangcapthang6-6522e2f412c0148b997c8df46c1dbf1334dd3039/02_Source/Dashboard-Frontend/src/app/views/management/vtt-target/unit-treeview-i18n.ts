import { Injectable } from '@angular/core';
import {I18n} from './i18n';
import {languages} from '../../../i18n/language-selector/languages.model';
import {DefaultTreeviewI18n} from './default-treeview-i18n';
import {TreeviewSelection} from 'ngx-treeview';
import {config} from '../../../config/application.config';

@Injectable()
export class UnitTreeviewI18n extends DefaultTreeviewI18n {
  constructor(
    protected i18n: I18n
  ) {
    super(i18n);
  }

  selections;

  getText(selection: TreeviewSelection): string {
    if (selection.uncheckedItems.length === 0) {
      return localStorage.getItem(config.user_default_language) === 'en' ? 'All' : 'Tất cả';
    }

    switch (selection.checkedItems.length) {
      case 0:
        return localStorage.getItem(config.user_default_language) === 'en' ? 'Select options' : 'Chọn';
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
    if (localStorage.getItem(config.user_default_language) === 'en') {
      return 'No datas found';
    } else {
      return 'Không có dữ liệu';
    }
  }
}
