import {TreeviewItem, TreeviewSelection} from 'ngx-treeview';
import {DefaultTreeviewI18n} from '../../../management/vtt-target/default-treeview-i18n';
import {config} from '../../../../config/application.config';

export class DropdownTreeview extends DefaultTreeviewI18n {
  private internalSelectedItem: TreeviewItem;

  set selectedItem(value: TreeviewItem) {
    if (value) {
      this.internalSelectedItem = value;
    } else {
      this.internalSelectedItem = null;
    }
  }

  get selectedItem(): TreeviewItem {
    return this.internalSelectedItem;
  }

  getText(selection: TreeviewSelection): string {
    return this.internalSelectedItem ? this.internalSelectedItem.text :
      (localStorage.getItem(config.user_default_language) === 'vi' ? 'Chọn' : 'Chose');
  }
}
