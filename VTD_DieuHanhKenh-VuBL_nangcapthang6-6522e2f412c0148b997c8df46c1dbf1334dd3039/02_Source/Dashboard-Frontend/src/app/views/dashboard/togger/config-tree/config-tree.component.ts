import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild} from '@angular/core';
import {DropdownTreeviewComponent, TreeviewConfig, TreeviewHelper, TreeviewI18n, TreeviewItem} from 'ngx-treeview';
import { isNil } from 'lodash';
import {DropdownTreeview} from './dropdown-treeview';

@Component({
  selector: 'app-config-tree',
  templateUrl: './config-tree.component.html',
  styleUrls: ['./config-tree.component.scss'],
  providers: [
    { provide: TreeviewI18n, useClass: DropdownTreeview }
  ]
})
export class ConfigTreeComponent implements OnChanges {
  @Input() config: TreeviewConfig;
  @Input() items: TreeviewItem[];
  @Input() value: any;
  @Output() valueChange = new EventEmitter<any>();
  @ViewChild(DropdownTreeviewComponent) dropdownTreeviewComponent: DropdownTreeviewComponent;
  filterText: string;
  private dropdownTreeviewSelectI18n: DropdownTreeview;

  constructor(public i18n: TreeviewI18n) {
    this.config = TreeviewConfig.create({
      hasAllCheckBox: false,
      hasCollapseExpand: false,
      hasFilter: true,
      maxHeight: 500
    });
    // @ts-ignore
    this.dropdownTreeviewSelectI18n = i18n as DropdownTreeview;
  }

  ngOnChanges(changes: SimpleChanges) {
    if (!isNil(this.value)) {
      this.updateSelectedItem();
    } else {
      this.selectItem(this.value);
    }
  }

  select(item: TreeviewItem) {
    // if (item.children === undefined) {
      this.selectItem(item);
    // }
  }

  private updateSelectedItem() {
    if (!isNil(this.items)) {
      const selectedItem = TreeviewHelper.findItemInList(this.items, this.value);
      if (selectedItem) {
        this.selectItem(selectedItem);
      } else {
        // this.selectItem(this.value);
      }
    }
  }

  private selectItem(item: TreeviewItem) {
    // this.value = null;
    this.dropdownTreeviewComponent.dropdownDirective.close();
    if (this.dropdownTreeviewSelectI18n.selectedItem !== item) {
      this.dropdownTreeviewSelectI18n.selectedItem = item;
      if (this.value !== item) {
        this.value = item.value;
        this.valueChange.emit(item.value);
      } else {
        // this.value = item.value;
        this.valueChange.emit(this.value);
      }
    }
  }

}
