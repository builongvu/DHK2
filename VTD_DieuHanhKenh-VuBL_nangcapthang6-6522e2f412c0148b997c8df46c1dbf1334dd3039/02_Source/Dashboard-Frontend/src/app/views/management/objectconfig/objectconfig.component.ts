import { Component, OnInit, TemplateRef } from '@angular/core';
import { ConfigobjectService } from '../../../services/management/configobject.service';
import { TreeItem, TreeviewItem } from 'ngx-treeview';
import { ObjectConfigModel, ObjectConfig } from '../../../models/objectconfig';
import { Pager } from '../../../models/Pager';
import { TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material';
import { DialogObjectComponent } from './dialog-object/dialog-object.component';
import { BsModalService, BsModalRef } from 'ngx-bootstrap';
import { config } from '../../../config/application.config';

@Component({
  selector: 'app-objectconfig',
  templateUrl: './objectconfig.component.html',
  styleUrls: ['./objectconfig.component.scss']
})
export class ObjectconfigComponent implements OnInit {
  mobjConfig = {
    hasFilter: true,
    hasCollapseExpand: true,
    maxHeight: 500,
  };
  mobjNodeTreeviewService: TreeviewItem;
  marrNodeTreeviewServices: TreeviewItem[] = [];
  marrData = [];
  marrObject: ObjectConfigModel;
  mstrParentObject = '';
  mstrstatus = 2;
  mblnDelete = false;
  mbrp = 1;
  marrParent: ObjectConfig[] = [];
  marrchild: ObjectConfigModel;
  mobjService: ObjectConfigModel;
  mobjNewObject: ObjectConfig = new ObjectConfig();
  mnbrGroupId;
  vdsChannelCode: string;
  mobjItemService: ObjectConfigModel;
  marrItemServices: ObjectConfigModel[] = [];
  marrIndexNode = [];
  mobjNodeItemService: TreeItem;
  marrNodeItemServices: TreeItem[] = [];
  mstrParent = '';
  mnbrP = 1;
  mobjPager: Pager;
  mnbrPageSize = 5;
  form;
  vcheckchild = false;
  mobjModalRef: BsModalRef;
  mblnConfirm: number;
  isCheckParent = false;
  changeParent = false;

  constructor(
    private configobjectService: ConfigobjectService,
    private toastr: ToastrService,
    private fb: FormBuilder,
    private translate: TranslateService,
    public dialog: MatDialog,
    private modalService: BsModalService
  ) { }

  ngOnInit() {
    this.mobjPager = new Pager(this.mnbrP, this.mnbrPageSize);
    this.getAllMenu();
  }



  getAllMenu() {
    this.configobjectService.getAllNotDelete().subscribe(
      vobjNext => {
        this.marrIndexNode = [];
        this.mobjService = new ObjectConfigModel();
        this.mobjService.id = -1;
        this.mobjService.objectType = '0';
        this.mobjService.status = 1;
        if (localStorage.getItem(config.user_default_language) === 'vi') {
          this.mobjService.objectName = 'Chức năng gốc';
        } else {
          this.mobjService.objectName = 'Origin';
        }
        this.marrNodeTreeviewServices = [];
        this.marrData = [];
        this.marrData = vobjNext['data'];
        this.marrData.reverse();
        this.marrData.push(this.mobjService);
        this.marrData.reverse();
        this.marrData.forEach((arr: ObjectConfigModel) => {
          if (arr.parentId === null) {
            arr.parentId = -1;
          }
        });
        this.marrParent = this.marrData.filter(item => item.objectType == '0');
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

  sort(marr: any[]) {
    marr.sort((left, right) => {
      if (left.objectName < right.objectName) { return -1; }
      if (left.objectName > right.objectName) { return 1; }
      return 0;
    });
  }

  createNode(parrItems: ObjectConfigModel[] = [], parrDataTree: ObjectConfigModel[]) {
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
  createTree(parrNodeTrees: ObjectConfigModel[], parrDataTree: ObjectConfigModel[]) {
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
    pobjNodeTree: ObjectConfigModel,
    pobjitem: TreeItem,
    parrItems: TreeItem[] = []
  ) {
    pobjitem = null;
    parrItems = [];
    if (pobjNodeTree.children) {
      pobjNodeTree.children.sort((left, right) => {
        if (left.ord < right.ord) { return -1; }
        if (left.ord > right.ord) { return 1; }
        return 0;
      });
      pobjNodeTree.children.forEach(value => {
        this.mobjNodeItemService = this.forwardData(value, null, []);
        parrItems.push(this.mobjNodeItemService);
      });
    }
    if (pobjNodeTree.children) {
      pobjitem = {
        value: pobjNodeTree.id,
        text: pobjNodeTree.objectName + '',
        children: parrItems,
        checked: false
      };
    } else {
      parrItems = [];
      pobjitem = {
        value: pobjNodeTree.id,
        text: pobjNodeTree.objectName + '',
        children: null,
        checked: false
      };
    }
    return pobjitem;
  }

  onValueChange(value: number) {
    this.marrData.forEach(tree => {
      if (tree.id == value) {
        this.marrObject = tree;
        this.marrObject.check = false;
        this.marrchild = new ObjectConfigModel();
        this.marrchild.children = [];
        this.marrchild.children.push(this.marrObject);
        this.marrchild.check = false;
        this.marrObject.children.forEach(child => {
          child.check = false;
          this.marrchild.children.push(child);
        });
      }
    });
    if (this.marrchild.parentId === null || value === -1) {
      this.mstrParent = this.translate.instant('management.menu.origin');
    } else {
      this.mstrParent = this.marrchild.children[0].objectName;
      this.mstrParentObject = this.marrData.filter(item => item.id == this.marrchild.children[0].parentId)[0].objectName;
    }
    this.mstrstatus = 2;
    this.mblnDelete = false;
  }

  pageChange(pnbrP: number) {
    if (this.isCheckParent == false) {
      if (this.vcheckchild == false) {
        this.marrchild.check = false;
        for (let i = 0; i < this.marrchild.children.length; i++) {
          this.marrchild.children[i].check = false;
        }
        this.mstrstatus = 2;
        this.mblnDelete = false;
      }
    }
    this.mnbrP = pnbrP;
    this.mobjPager = new Pager(this.mnbrP, this.mnbrPageSize);
  }


  newObject() {
    this.mobjNewObject = new ObjectConfig();
    this.mobjNewObject.id = -1;
    const vdialog = this.dialog.open(DialogObjectComponent, {
      data: {
        obj: this.mobjNewObject,
        parent: this.marrchild,
        marr: this.marrData
      }
    });
    vdialog.afterClosed().subscribe(result => {
      this.mnbrP = 1;
      this.getAllMenu();
    });
  }

  edit(mobj: ObjectConfigModel) {
    this.mobjNewObject = new ObjectConfig();
    this.mobjNewObject.status = mobj.status;
    this.mobjNewObject.parentId = mobj.parentId;
    this.mobjNewObject.functionType = mobj.functionType;
    this.mobjNewObject.id = mobj.id;
    this.mobjNewObject.objectCode = mobj.objectCode;
    this.mobjNewObject.objectIcon = mobj.objectIcon;
    this.mobjNewObject.objectImg = mobj.objectImg;
    this.mobjNewObject.objectName = mobj.objectName;
    this.mobjNewObject.objectNameI18N = mobj.objectNameI18N;
    this.mobjNewObject.objectType = mobj.objectType;
    this.mobjNewObject.objectUrl = mobj.objectUrl;
    this.mobjNewObject.ord = mobj.ord;
    const vdialog = this.dialog.open(DialogObjectComponent, {
      data: {
        obj: this.mobjNewObject,
        parent: this.marrchild,
        marr: this.marrData.filter(item => item.objectType == 0)
      }
    });
    vdialog.afterClosed().subscribe(result => {
      this.mnbrP = 1;
      this.getAllMenu();
    });
  }

  parentChange(value: number) {
    this.mobjNewObject.parentId = value;
  }

  /**
 * Thông báo thành công
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  showSuccess(message: string) {
    this.toastr.success(message,
      this.translate.instant('management.warningconfig.success'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }

  /**
 * Thông báo thất bại(Lỗi)
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  showError(message: string) {
    this.toastr.error(message,
      this.translate.instant('management.warningconfig.fail'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }


  SelectAll() {
    this.isCheckParent = false;
    let vblnCheck = false;
    if (this.marrchild.check === false) {
      vblnCheck = true;
    }
    let total = this.mnbrP * 10;
    if (this.mnbrP * 10 > this.marrchild.children.length) {
      total = this.marrchild.children.length;
    }
    if (vblnCheck === true) {
      for (let i = (this.mnbrP - 1) * 10; i < total; i++) {
        this.marrchild.children[i].check = true;
      }
    } else {
      this.marrchild.children.forEach(child => {
        child.check = false;
      });
      this.vcheckchild = false;
    }
    this.CheckStatus();
  }

  selectChild() {
    let vblnCheck = false;
    this.isCheckParent = true;
    if (this.marrchild.children[0].check === false) {
      vblnCheck = true;
    }
    for (let i = 0; i < this.marrchild.children.length; i++) {
      if (vblnCheck === true) {
        this.marrchild.check = true;
        this.marrchild.children[i].check = true;
        this.vcheckchild = true;
      } else {
        this.marrchild.check = false;
        this.marrchild.children[i].check = false;
        this.vcheckchild = false;
      }
    }
    this.CheckStatus();
  }


  /**
  * Sự kiện khi click checkbox chọn các cấu hình cảnh báo
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2019/11/18
  */
  select(id: number) {
    for (let i = 0; i < this.marrchild.children.length; i++) {
      if (this.marrchild.children[i].id === id) {
        if (this.marrchild.children[i].check === true) {
          this.marrchild.children[i].check = false;
        } else {
          this.marrchild.children[i].check = true;
        }
        break;
      }
    }
    let vblncheck1 = true;
    for (let i = 1; i < this.marrchild.children.length; i++) {
      if (this.marrchild.children[i].check === false) {
        vblncheck1 = false;
      }
    }
    if (vblncheck1) {
      this.marrchild.children[0].check = true;
      this.vcheckchild = true;
    } else {
      this.marrchild.children[0].check = false;
      this.vcheckchild = false;
    }
    let vblncheck = true;
    let total = this.mnbrP * 10;
    if (this.mnbrP * 10 > this.marrchild.children.length) {
      total = this.marrchild.children.length;
    }
    for (let i = (this.mnbrP - 1) * 10; i < total; i++) {
      if (this.marrchild.children[i].check === false) {
        vblncheck = false;
      }
    }
    if (vblncheck) {
      this.marrchild.check = true;
    } else {
      this.marrchild.check = false;
    }
    this.CheckStatus();
  }

  /**
  * Kiểm tra các cấu hình được chọn và cho phép mở khoá/khoá
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2019/11/18
  */
  CheckStatus() {
    this.mstrstatus = 0;
    const marr = this.marrchild.children.filter(item => item.check === true);
    if (marr.length > 0) {
      this.mstrstatus = marr[0].status;
      for (let i = 1; i < marr.length; i++) {
        if (marr[i].status !== this.mstrstatus) {
          this.mstrstatus = -1;
          break;
        }
      }
      this.mblnDelete = true;
    } else {
      this.mblnDelete = false;
      this.mstrstatus = 2;
    }
  }

  openConfirm(pobjTemplate: TemplateRef<any>, type: number) {
    if (this.marrchild.children.length > 1) {
      if (this.marrchild.children[0].check == true) {
        this.changeParent = true;
      } else {
        this.changeParent = false;
      }
    }
    this.mblnConfirm = type;
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    });
  }

  back() {
    this.mobjModalRef.hide();
  }

  change(status: number, onlychild: boolean) {
    let varrUnLock = this.marrchild.children.filter(item => item.check === true);
    varrUnLock.reverse();
    let isParent = false;
    const listObj: ObjectConfig[] = [];
    let size = varrUnLock.length;
    if (onlychild) {
      size = size - 1;
    }
    for (let i = 0; i < size; i++) {
      let marrchilds = null;
      if (varrUnLock[i].id == this.marrchild.children[0].id) {
        isParent = true;
      }
      if (status == -1 && isParent == false) {
        this.marrData.forEach(tree => {
          if (tree.id == varrUnLock[i].id) {
            marrchilds = tree;
          }
        });
        if (this.marrchild.children.length == varrUnLock.length && isParent) {

        } else if (marrchilds.children && marrchilds.children.length > 0) {
          this.getAllMenu();
          this.showError(this.translate.instant('management.menu.cantdelete'));
          this.back();
          this.marrchild.check = false;
          this.mstrstatus = 2;
          this.mblnDelete = false;
          return;
        }
      }
      let vobj = new ObjectConfig();
      vobj.status = status;
      vobj.parentId = varrUnLock[i].parentId;
      if (vobj.parentId == -1) {
        vobj.parentId = null;
      }
      vobj.functionType = varrUnLock[i].functionType;
      vobj.id = varrUnLock[i].id;
      vobj.objectCode = varrUnLock[i].objectCode;
      vobj.objectIcon = varrUnLock[i].objectIcon;
      vobj.objectImg = varrUnLock[i].objectImg;
      vobj.objectName = varrUnLock[i].objectName;
      vobj.objectNameI18N = varrUnLock[i].objectNameI18N;
      vobj.objectType = varrUnLock[i].objectType;
      vobj.objectUrl = varrUnLock[i].objectUrl;
      vobj.ord = varrUnLock[i].ord;
      if (vobj.id != -1) {
        listObj.push(vobj);
      }
    }
    this.configobjectService.saveObject(listObj).subscribe(res => {
      if (res.code === 200) {
        if (status === 1) {
          this.showSuccess(this.translate.instant('management.menu.unlockSuccess'));
        } else if (status == 0) {
          this.showSuccess(this.translate.instant('management.menu.lockSuccess'));
        } else {
          this.showSuccess(this.translate.instant('management.menu.deleteSuccess'));
        }
        this.marrchild.check = false;
        this.mstrstatus = 2;
        this.mblnDelete = false;
        if (isParent && status == -1) {
          this.marrchild = null;
        }
        this.getAllMenu();
      } else {
        this.showError(res.errors);
      }
      this.back();
    });
  }
}
