import { Injectable } from '@angular/core';
import { TreeviewItem, TreeviewSelection } from 'ngx-treeview';
import { DefaultTreeviewI18n } from '../../../vtt-target/default-treeview-i18n';
import { I18n } from '../../../vtt-target/i18n';
import { config } from '../../../../../config/application.config';

@Injectable()
export class DropdownTreeview extends DefaultTreeviewI18n {
  private internalSelectedItem: TreeviewItem;

  constructor(protected i18n: I18n) {
    super(i18n); 
  }
  
  set selectedItem(value: TreeviewItem) {
    if (value) {
      this.internalSelectedItem = value;
    }
  }

  get selectedItem(): TreeviewItem {
    return this.internalSelectedItem;
  }

  getText(selection: TreeviewSelection): string {
    return this.internalSelectedItem ? this.internalSelectedItem.text : (localStorage.getItem(config.user_default_language) === 'en' ? "Select" : "Chọn");
  }
  
  getFilterNoItemsFoundText(): string {
    if (localStorage.getItem(config.user_default_language) === 'en') {
      return 'No datas found';
    } else {
      return 'Không có dữ liệu';
    }
  }
}
