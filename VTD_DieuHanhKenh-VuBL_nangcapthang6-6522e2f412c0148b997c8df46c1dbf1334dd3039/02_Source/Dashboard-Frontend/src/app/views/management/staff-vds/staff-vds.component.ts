import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder} from '@angular/forms';
import {TreeItem, TreeviewConfig, TreeviewItem} from 'ngx-treeview';
import {UnitModel} from '../../../models/unit.model';
import {ServiceService} from '../../../services/management/service.service';
import {TreeVDSService} from '../../../services/management/tree-VDS.service';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';

@Component({
  selector: 'app-staff-vds',
  templateUrl: './staff-vds.component.html',
  styleUrls: ['./staff-vds.component.scss']
})
export class StaffVdsComponent implements OnInit, OnDestroy {

  constructor(private fb: FormBuilder,
              private serviceService: ServiceService,
              private treeVDSService: TreeVDSService) {
  }

  nodeTreeView: TreeviewItem;
  nodeTreeViews: TreeviewItem[] = [];
  nodeItem: TreeItem;
  nodeItems: TreeItem[] = [];
  dataTree = [];
  dataUnit = [];
  nodeTrees: UnitModel[] = [];
  unitIitem: UnitModel;
  valueUnit;
  mobjNodeItemService: TreeItem;
  marrIndexNodeService = [];
  mobjConfigVDS = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 850,
  });
  valueVds;
  unsubscribe$ = new Subject();

  ngOnInit() {
    this.unitTreeData('', 'VDS');
    this.serviceService.reloadWarning$
    // .pipe(takeUntil(this.unsubscribe$))
      .subscribe(res => {
        // console.log(res);
        if (res) {
          // console.log('ddddddddddddddd');
          // this.currentP = 1;
          this.unitTreeData('');
        }
      });
  }

  no() {

  }

  unitTreeData(searchText: string, defaultValue?: string) {
    this.nodeTrees = [];
    this.nodeTreeViews = [];
    this.treeVDSService.getunitsVDS(searchText).subscribe(
      next => {
        // console.log(next['data']);
        if (!next['data']) {
          return;
        }

        this.nodeTrees = [];
        this.nodeTreeViews = [];
        this.dataUnit = next['data'];
        this.dataTree = next['data'];

        this.createNode(this.nodeTrees, this.dataTree, this.unitIitem).forEach(
          note => {
            this.nodeTrees.push(note);
          }
        );
        // console.log('mang', this.nodeTrees);
        setTimeout(() => {
          this.dataTree = this.nodeTrees;
          setTimeout(() => {
            this.createTree(this.nodeTrees, this.dataTree);
            setTimeout(() => {
              this.nodeTrees.forEach(valuess => {
                this.nodeTreeView = new TreeviewItem(this.forwardData(valuess, this.nodeItem, this.nodeItems));
                this.nodeTreeViews.push(this.nodeTreeView);
              });
              this.valueVds = defaultValue;
            }, 2);
          }, 2);
        }, 5);
        // console.log('cay moi', this.nodeTreeViews);
      }
    );


  }

  forwardData(pobjNodeTree: UnitModel, pobjitem: TreeItem, parrItems: TreeItem[] = []) {
    try {
      pobjitem = null;
      parrItems = [];
      // pobjNodeTree = null;
      if (pobjNodeTree.children) {
        pobjNodeTree.children.forEach(value => {
          this.mobjNodeItemService = this.forwardData(value, null, []);
          parrItems.push(this.mobjNodeItemService);
        });
      }
      if (pobjNodeTree.children) {
        pobjitem = {
          value: pobjNodeTree.shopCode,
          text: pobjNodeTree.shopCode + ' - ' + pobjNodeTree.shopName
            + (pobjNodeTree.shortName ? '(' + pobjNodeTree.shortName + ')' : ''),
          children: parrItems,
          checked: false
        };
      } else {
        parrItems = [];
        pobjitem = {
          value: pobjNodeTree.shopCode,
          text: pobjNodeTree.shopCode + ' - ' + pobjNodeTree.shopName + pobjNodeTree.shortName,
          children: null,
          checked: false
        };
      }
      return pobjitem;
    } catch (e) {
      console.log(e);
    }
  }

  createNode(parrItems: UnitModel[] = [], parrDataTree: UnitModel[], pobjItem: UnitModel) {
    try {
      pobjItem = null;
      parrItems = parrDataTree.map(value => {
        pobjItem = {
          id: value.id,
          shopName: value.shopName,
          parentShopCode: value.parentShopCode,
          shopCode: value.shopCode,
          vdsChannelCode: value.vdsChannelCode,
          shortName: value.shortName,
          children: [],
          groupId: value.groupId
        };
        return pobjItem;
      });
      return parrItems;
    } catch (e) {
      console.log(e);
    }
  }

  createTree(parrNodeTrees: UnitModel[], parrDataTree: UnitModel[]) {
    this.marrIndexNodeService = [];
    try {
      const len = parrNodeTrees.length;
      for (let i = 0; i < len; i++) {
        for (let j = 0; j < len; j++) {
          if (parrNodeTrees[i].shopCode === parrDataTree[j].parentShopCode) {
            parrNodeTrees[i].children.push(parrDataTree[j]);
            this.marrIndexNodeService.push(j);
          }
        }
      }
      // console.log('truoc', parrNodeTrees);
      const c = (a: number, b: number) => (a - b);
      this.marrIndexNodeService.sort(c);
      for (let i = this.marrIndexNodeService.length - 1; i >= 0; i--) {
        parrNodeTrees.splice(this.marrIndexNodeService[i], 1);
      }
      // console.log('sau', parrNodeTrees);
    } catch (e) {
      console.log(e);
    }
  }

  unitChange(value: string) {
    this.valueUnit = value;
    this.dataUnit.forEach(
      tree => {
        if (tree.shopCode === value) {
          this.treeVDSService.setUnit(tree);
        }
      }
    );


  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  searchTree(value: string) {
    console.log('filter text', value);
    this.unitTreeData(value);
  }
}
