import { config } from './../../../../config/application.config';
import { TranslateService } from '@ngx-translate/core';
import { UnitService } from './../../../../services/management/unit.service';
import { ServiceService } from './../../../../services/management/service.service';
import { ServiceModel } from './../../../../models/service.model';
import { PlanmonthlyModel } from './../../../../models/Planmonthly.model';

import { TreeviewItem, TreeItem } from 'ngx-treeview';
import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { Output } from '@angular/core';
import { EventEmitter } from 'events';

@Component({
  selector: 'app-mng-target',
  templateUrl: './mng-target.component.html',
  styleUrls: ['./mng-target.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class MngTargetComponent implements OnInit {
  @Output() send = new EventEmitter();

  mobjConfig = {
    hasFilter: true,
    hasCollapseExpand: true,
    maxHeight: 850
  };

  mobjNodeTreeviewService: TreeviewItem;
  marrNodeTreeviewServices: TreeviewItem[] = [];
  marrData = [];
  mobjService: ServiceModel;
  mnbrGroupId;
  vdsChannelCode: string;
  mobjItemService: ServiceModel;
  marrItemServices: ServiceModel[] = [];
  marrIndexNode = [];
  mobjNodeItemService: TreeItem;
  marrNodeItemServices: TreeItem[] = [];
  marrServiceIds: number[] = [];
  mstrKeyWord: string;
  mblnChec = false;
  mobTarget;

  marrNodeTreeviewServicesStatus: TreeviewItem[] = [];
  marrDataStatus = [];
  mobjServiceStatus: ServiceModel;
  marrItemServicesStatus: ServiceModel[] = [];
  mobjNodeItemServiceStatus: TreeItem;
  marrNodeItemServicesStatus: TreeItem[] = [];

  constructor(private serviceService: ServiceService, private translate: TranslateService) { }

  ngOnInit() {

    this.serviceService.getAllService().subscribe((data: any) => {
      this.serviceService.setAllService(data);
      this.getAllService();
    });

    this.serviceService.getAllServiceByStatus().subscribe((data: any) => {
      this.serviceService.setAllServiceStatus(data);
      this.getAllServiceByStatus();
    });

  }

  /**
   * Lấy toàn bộ chỉ tiêu từ api
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 2019/09/13
   * @return: Toàn bộ chỉ tiêu
   */
  getAllService() {
    this.serviceService.allService$.subscribe(
      vobjNext => {
        this.marrNodeTreeviewServices = [];
        this.marrData = [];
        this.marrIndexNode = [];
        this.marrData = vobjNext['data'];
        const vobjService: ServiceModel = new ServiceModel(null, null);
        vobjService.id = 0;
        if (localStorage.getItem(config.user_default_language) === 'en') {
          vobjService.name = 'Origin';
        } else {
          vobjService.name = 'Chỉ tiêu gốc';
        }
        vobjService.code = '';
        this.marrData = this.marrData.filter(item => item.id !== 0);
        this.marrData.push(vobjService);

        const vobjService1: ServiceModel = new ServiceModel(null, null);
        vobjService1.id = -1;
        vobjService1.name = '';
        vobjService1.code = '';
        this.marrData = this.marrData.filter(item => item.id !== -1);
        this.marrData.push(vobjService1);
        this.marrData.sort((left, right) => {
          if (left.id < right.id) { return -1; }
          if (left.id > right.id) { return 1; }
          return 0;
        });

        this.marrItemServices = this.createNode(
          this.marrItemServices,
          this.marrData
        );
        this.createTree(this.marrItemServices, this.marrData);

        this.marrItemServices.forEach(vobjValue => {
          this.mobjNodeTreeviewService = new TreeviewItem(
            this.forwardData(
              vobjValue,
              this.mobjNodeItemService,
              this.marrNodeItemServices
            )
          );
          this.marrNodeTreeviewServices.push(this.mobjNodeTreeviewService);
        });
      },
      vobjErorr => {
        console.log('no data');
      }
    );
  }

  getAllServiceByStatus() {
    // console.log('aaaaaa')
    this.marrNodeTreeviewServicesStatus = [];
    this.marrDataStatus = [];
    this.mobjService = new ServiceModel(null, '');
    this.serviceService.allServiceStatus$.subscribe(
      vobjNext => {
        this.marrNodeTreeviewServicesStatus = [];
        this.marrDataStatus = [];
        this.marrIndexNode = [];
        this.marrDataStatus = vobjNext['data'];
        const vobjService: ServiceModel = new ServiceModel(null, null);
        vobjService.id = 0;
        if (localStorage.getItem(config.user_default_language) === 'en') {
          vobjService.name = 'Origin';
        } else {
          vobjService.name = 'Chỉ tiêu gốc';
        }
        vobjService.code = '';
        this.marrDataStatus = this.marrDataStatus.filter(item => item.id !== 0);
        this.marrDataStatus.push(vobjService);

        this.marrDataStatus.sort((left, right) => {
          if (left.id < right.id) { return -1; }
          if (left.id > right.id) { return 1; }
          return 0;
        });

        this.marrItemServicesStatus = this.createNode(
          this.marrItemServicesStatus,
          this.marrDataStatus
        );
        this.createTree(this.marrItemServicesStatus, this.marrDataStatus);

        this.marrItemServicesStatus.forEach(vobjValue => {
          this.mobjNodeTreeviewService = new TreeviewItem(
            this.forwardData(
              vobjValue,
              this.mobjNodeItemServiceStatus,
              this.marrNodeItemServicesStatus
            )
          );
          this.marrNodeTreeviewServicesStatus.push(this.mobjNodeTreeviewService);
        });
        this.serviceService.setServiceTree(this.marrNodeTreeviewServicesStatus);

      },
      vobjErorr => {
        console.log('no data');
      }
    );
  }

  /**
   * Tạo Mảng các đối tướng trong tree
   *
   * @author: TruyenNH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: parrItems - danh sách Object mới
   * @param: parrDataTree - danh sách Object chỉ tiêu từ api
   * @return: parrItems - danh sách Object mới
   */
  createNode(parrItems: ServiceModel[] = [], parrDataTree: ServiceModel[]) {
    parrItems = parrDataTree.map(vobjValue => {
      this.mobjItemService = vobjValue;
      this.mobjItemService.children = [];
      return this.mobjItemService;
    });
    return parrItems;
  }

  /**
   * Tạo tree theo Object ServiceModel
   *
   * @author: TruyenNH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: parrNodeTrees - Danh sách Object mới sau khi xử lý
   * @param: parrDataTree - Danh sách Object chưa xử lý
   */
  createTree(parrNodeTrees: ServiceModel[], parrDataTree: ServiceModel[]) {
    const vnbrLen = parrNodeTrees.length;
    for (let i = 0; i < vnbrLen; i++) {
      for (let j = 0; j < parrDataTree.length; j++) {
        if (parrNodeTrees[i].id === parrDataTree[j].parentId) {
          parrNodeTrees[i].children.push(parrDataTree[j]);
          this.marrIndexNode.push(j);
        }
      }
    }
    // console.log('arrrrr: ', this.marrIndexNode);
    const vnbrC = (pnbrA: number, pnbrB: number) => pnbrA - pnbrB;
    this.marrIndexNode.sort(vnbrC);
    for (let vnbrI = this.marrIndexNode.length - 1; vnbrI >= 0; vnbrI--) {
      parrNodeTrees.splice(this.marrIndexNode[vnbrI], 1);
    }
    // console.log('arrrrr222: ', this.marrIndexNode);
  }

  /**
   * Chuyển đổi tree: ServiceModel sang TreeViewItem
   *
   * @author: TruyenNH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: pobjNodeTree - Object tham chiếu hiện tại
   * @param: pobjitem - Object TreeItem
   * @param: parrItems - Danh sách Object TreeViewItem
   */
  forwardData(
    pobjNodeTree: ServiceModel,
    pobjitem: TreeItem,
    parrItems: TreeItem[] = []
  ) {
    pobjitem = null;
    parrItems = [];
    if (pobjNodeTree.children) {
      pobjNodeTree.children.forEach(value => {
        this.mobjNodeItemService = this.forwardData(value, null, []);
        parrItems.push(this.mobjNodeItemService);
      });
    }
    if (pobjNodeTree.children) {
      if (pobjNodeTree.id > 0) {
        pobjitem = {
          value: pobjNodeTree.id,
          text: pobjNodeTree.code + ' - ' + pobjNodeTree.name,
          children: parrItems,
          checked: false
        };
      } else {
        pobjitem = {
          value: pobjNodeTree.id,
          text: pobjNodeTree.code + pobjNodeTree.name,
          children: parrItems,
          checked: false
        };
      }
    } else {
      parrItems = [];
      pobjitem = {
        value: pobjNodeTree.id,
        text: pobjNodeTree.name,
        children: null,
        checked: false
      };
    }
    return pobjitem;
  }

  selectTree($event: number[]) {
    this.marrServiceIds = [];
    this.marrServiceIds = $event;
    this.marrNodeTreeviewServices.forEach(vobjValue => {
      if (vobjValue.checked) {
        this.marrServiceIds.push(vobjValue.value);
      }
      if (!vobjValue.children) {
      } else {
        vobjValue.children.forEach(vobjChid => {
          if (vobjChid.checked) {
            this.marrServiceIds.push(vobjChid.value);
          }
        });
      }
    });
    this.uniqueElementInArray(this.marrServiceIds);
    // console.log('serviceIds: ', this.uniqueElementInArray(this.marrServiceIds));
  }

  /**
   * Xóa bỏ những phần tử lặp trong mảng
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: parrArray - Mảng cần xóa phần tử lặp
   */
  uniqueElementInArray(parrArray: number[]) {
    for (let i = 0; i < parrArray.length - 1; i++) {
      for (let j = i + 1; j < parrArray.length; j++) {
        if (parrArray[i] === parrArray[j]) {
          parrArray.splice(j, 1);
        }
      }
    }
    return parrArray;
  }

  /**
   * Filter trong treeview chỉ tiêu
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: parrArray - Mảng dùng để tìm kiếm
   * @param: $event - keyword tìm kiếm
   */
  filterTreeView(parrArray: any[], $event: string) {
    this.mstrKeyWord = $event;

    if (!parrArray) {
      return [];
    }

    return parrArray.filter(
      vstrData => vstrData[this.mstrKeyWord].toLowerCase() > -1
    );
  }

  /**
 * Truyền dữ liệu Service kiểu tree sang component target-detail
 *
 * @author: Manhtd
 * @version: 1.0
 * @since: 11/2019
 * @param: value của chỉ tiêu click
 */
  onValueChange(value: string) {
    this.serviceService.setTreeData(this.marrData);
    // console.log('re')
    this.marrData.forEach(tree => {
      if (tree.id == value) {
        this.mobTarget = tree;
        this.serviceService.setService(this.mobTarget);
      }
    });
  }
}
