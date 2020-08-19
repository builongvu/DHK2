import { Injectable } from '@angular/core';
import {TreeviewI18n, TreeviewSelection} from 'ngx-treeview';
import {I18n} from './i18n';
import {config} from '../../../config/application.config';

@Injectable()
export class DefaultTreeviewI18n extends TreeviewI18n {
  constructor(
    protected i18n: I18n
  ) {
    super();
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

  getAllCheckboxText(): string {
    if (localStorage.getItem(config.user_default_language) === 'en') {
      return 'All';
    } else {
      return 'Tất cả';
    }
  }

  getFilterPlaceholder(): string {
    if (localStorage.getItem(config.user_default_language) === 'en') {
      return 'Search';
    } else {
      return 'Tìm kiếm';
    }
  }

  getFilterNoItemsFoundText(): string {
    if (localStorage.getItem(config.user_default_language) === 'en') {
      return 'No items found';
    } else {
      return 'Không có dữ liệu';
    }
  }

  getTooltipCollapseExpandText(isCollapse: boolean): string {
    return isCollapse
      ? localStorage.getItem(config.user_default_language) === 'en' ? 'Expand' : 'Mở rộng'
      : localStorage.getItem(config.user_default_language) === 'en' ? 'Collapse' : 'Thu lại';
  }
}
