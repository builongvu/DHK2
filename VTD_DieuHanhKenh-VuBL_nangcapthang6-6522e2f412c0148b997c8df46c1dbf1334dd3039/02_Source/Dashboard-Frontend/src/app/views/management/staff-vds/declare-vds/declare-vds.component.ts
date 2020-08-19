import {Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {TreeVDSService} from '../../../../services/management/tree-VDS.service';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE, MatDialog} from '@angular/material';
import {UpdateDeclareVdsComponent} from './update-declare-vds/update-declare-vds.component';
import {config} from '../../../../config/application.config';
import {DeclareVDSModel} from '../../../../models/declareVDS.model';
import * as _moment from 'moment';
import {MomentDateAdapter} from '@angular/material-moment-adapter';
import {TreeItem, TreeviewI18n, TreeviewItem} from 'ngx-treeview';
import {UnitTreeviewI18n} from '../../vtt-target/unit-treeview-i18n';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from 'ngx-toastr';
import {AddDeclareVdsComponent} from './add-declare-vds/add-declare-vds.component';
import {BsModalRef, BsModalService} from 'ngx-bootstrap';
import {UnitModel} from '../../../../models/unit.model';
import {ServiceService} from '../../../../services/management/service.service';
import {StaffVdsComponent} from '../staff-vds.component';
import {ToggerComponent} from '../../../dashboard/togger/togger.component';

export const MY_FORMATS_DATE = {
  parse: {
    dateInput: 'DD/MM/YYYY',
  },
  display: {
    dateInput: 'DD/MM/YYYY',
    monthYearLabel: 'DDD MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'DDD MMMM YYYY',
  },
};

@Component({
  selector: 'app-declare-vds',
  templateUrl: './declare-vds.component.html',
  styleUrls: ['./declare-vds.component.scss'],
  providers: [
    {provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE]},
    {provide: MAT_DATE_FORMATS, useValue: MY_FORMATS_DATE},
    {provide: TreeviewI18n, useClass: UnitTreeviewI18n}
  ],
})
export class DeclareVdsComponent implements OnInit {

  declareVds: FormGroup;

  constructor(private fb: FormBuilder,
              private treeVDSService: TreeVDSService,
              public dialog: MatDialog,
              private serviceService: ServiceService,
              private modalService: BsModalService,
              private translate: TranslateService,
              private toastr: ToastrService) {
    this.createForm();
  }

  aaa: string;

  groupVDS;
  dataTables;
  currentP = 1;
  pageSize = config.pageSize;
  showButtonAdd = true;
  dataUnit;
  dataSelect;
  shopCode;
  modelAction: DeclareVDSModel;
  private mobjModalRef: BsModalRef;
  clickSearch = false;

  nodeTreeView: TreeviewItem;
  nodeTreeViews: TreeviewItem[] = [];
  nodeItem: TreeItem;
  nodeItems: TreeItem[] = [];
  dataTree = [];
  nodeTrees: UnitModel[] = [];
  unitIitem: UnitModel;
  mobjNodeItemService: TreeItem;
  marrIndexNodeService = [];

  ngOnInit() {
    this.unitTreeData();
    this.clickSearch = false;
    // this.getComboGroup();
    this.getData();
    this.getUnitTrees();
  }

  getData() {
    this.treeVDSService.service$.subscribe(
      data => {
        console.log('unit data', data);
        this.dataSelect = data;
        this.aaa = data['shopName'];
        if (data['shopCode']) {
          this.shopCode = data['shopCode'];
          // if (this.shopCode !== 'VDS') {
          //   this.declareVds.controls['groupVds'].disable();
          // } else if (this.shopCode === 'VDS') {
          //   this.declareVds.controls['groupVds'].enable();
          // }
          this.getComboGroup();
          this.showButtonAdd = false;
          this.declareVds.controls['status'].setValue(1);
        } else {
          this.showButtonAdd = true;
        }

        this.declareVds.controls['parentName'].setValue(this.aaa);
        this.declareVds.controls['unitCode'].setValue(null);
        this.declareVds.controls['shortName'].setValue(null);
        this.declareVds.controls['unitName'].setValue(null);
        this.declareVds.controls['status'].setValue(null);
        this.declareVds.controls['mdtDateFrom'].setValue(null);
        this.declareVds.controls['mdtDateTo'].setValue(null);

        this.currentP = 1;
        this.loadData();

        // this.treeVDSService.getDeclareDatas(data['shopCode'], 1).subscribe(
        //   table => {
        //     // console.log('shop code', data['shopCode']);23
        //     console.log('table', table['data']);
        //     this.dataTables = table['data'];
        //   }
        // );
      }
    );
  }

  loadData() {
    this.clickSearch = false;
    this.treeVDSService.getDeclareDatas(this.shopCode, this.declareVds.controls['child'].value).subscribe(
      table => {
        // console.log('shop code', data['shopCode']);23
        console.log('table', table['data']);
        this.dataTables = table['data'];
      }
    );
  }

  getCreateDate(createDate: any) {
    const dateNum = Date.parse(createDate);
    if (!createDate) {
      return null;
    } else {
      const date = new Date(createDate);
      const options = {
        year: 'numeric', month: 'numeric', day: 'numeric',
      };

      return date.toLocaleDateString('vi', options);
    }

  }

  createForm() {
    this.declareVds = this.fb.group({
      child: 0,
      unitCode: [''],
      unitName: [''],
      shortName: [''],
      parentName: [''],
      groupVds: [{value: null, disabled: true}],
      status: [''],
      mdtDateFrom: new FormControl(''),
      mdtDateTo: new FormControl(''),
    });
  }

  onChangeform() {

  }

  getUnitTrees() {
    this.treeVDSService.getunitsVDS('').subscribe(
      next => {
        // console.log(next['data']);
        if (!next['data']) {
          return;
        }
        this.dataUnit = next['data'];
      }
    );
  }

  search() {
    this.currentP = 1;
    this.submit();
  }

  submit() {
    this.clickSearch = true;
    let parentShopCode;
    this.dataUnit.forEach(
      data => {
        if (this.declareVds.controls['parentName'].value == data['shopName']) {
          parentShopCode = data['shopCode'];
        }
      }
    );

    let fromDate;
    let toDate;
    if (!this.declareVds.controls['mdtDateFrom'].value) {
      fromDate = null;
    } else {
      fromDate = this.declareVds.controls['mdtDateFrom'].value.getTime();
    }

    if (!this.declareVds.controls['mdtDateTo'].value) {
      toDate = null;
    } else {
      toDate = this.declareVds.controls['mdtDateTo'].value.getTime();
    }

    let vdsChannelCode;
    if (this.declareVds.controls['groupVds'].value == '') {
      vdsChannelCode = null;
    } else {
      vdsChannelCode = this.declareVds.controls['groupVds'].value;
    }
    const status = this.declareVds.controls['status'].value === 'null' ? null :
      this.declareVds.controls['status'].value;

    this.declareVds.controls['groupVds'].setValue(null);
    const model: DeclareVDSModel = new DeclareVDSModel(
      null,
      this.declareVds.controls['unitCode'].value,
      this.declareVds.controls['unitName'].value,
      this.declareVds.controls['shortName'].value,
      parentShopCode,
      status,
      null, null, null);
    console.log('model create: ', model);
    this.treeVDSService.searchVDS(model).subscribe(
      data => {
        console.log('data search: ', data['data']);
        this.dataTables = data['data'];
      }
    );
  }

  showModalEdit(row: any) {
    const dialogRef = this.dialog.open(UpdateDeclareVdsComponent, {
      maxWidth: '85vw',
      maxHeight: '100vh',
      width: '75vw',
      data: {
        data: row,
        tree: this.nodeTreeViews,
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      this.clickSearch ? this.submit() : this.loadData();
      // this.loadData();
    });
  }

  showModalAdd() {
    const dialogRef = this.dialog.open(AddDeclareVdsComponent, {
      maxWidth: '85vw',
      maxHeight: '95vh',
      width: '75vw',
      data: {
        dataSelect: this.dataSelect,
        shopCode: this.shopCode
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      // this.currentP = 1;
      this.serviceService.reloadWarning$.subscribe(res => {
        if (res) {
          this.currentP = 1;
          // this.treeVDSService.reloadTree(res);
        }
      });
      this.clickSearch ? this.submit() : this.loadData();
      // this.loadData();
    });
  }

  getComboGroup() {
    this.treeVDSService.getGroupVDS(this.shopCode).subscribe(
      data => {
        this.groupVDS = data['data'];
        // console.log(data);
        if (data['data'].length === 1) {
          this.declareVds.controls['groupVds'].setValue(data['data'][0]['code']);
        }
        // console.log(data['data']);
      }
    );
  }

  pageChange(value: number) {
    this.currentP = value;
  }

  clickRow(table: any) {
    console.log('row click', table);
    this.declareVds.controls['unitCode'].setValue(table['shopCode']);
    this.declareVds.controls['shortName'].setValue(table['shortName']);
    this.declareVds.controls['unitName'].setValue(table['shopName']);
    this.declareVds.controls['groupVds'].setValue(table['vdsChannelCode']);
    this.declareVds.controls['status'].setValue(table['status']);
    this.declareVds.controls['parentName'].setValue(table['parentShopName']);
    if (table['fromDate']) {
      this.declareVds.controls['mdtDateFrom'].setValue(new Date(table['fromDate']));
    } else {
      this.declareVds.controls['mdtDateFrom'].setValue(null);
    }

    if (table['toDate']) {
      console.log('to date', table['toDate']);
      // this.declareVds.controls['mdtDateTo'].setValue(new Date());
      this.declareVds.controls['mdtDateTo'].setValue(new Date(table['toDate']));
    } else {
      this.declareVds.controls['mdtDateTo'].setValue(null);
    }
  }

  dateChangeFrom(value: any) {
    console.log('date from: ', value.value._d);
    if (value.value) {
      this.declareVds.controls['mdtDateFrom'].setValue(new Date(value.value._d.getTime()));
    } else {
      this.declareVds.controls['mdtDateFrom'].setValue(null);
    }
  }

  dateChangeTo(value: any) {

    if (value.value) {
      this.declareVds.controls['mdtDateTo'].setValue(new Date(value.value._d.getTime()));
    } else {
      this.declareVds.controls['mdtDateTo'].setValue(null);
    }
  }

  openModal(pobjTemplate: TemplateRef<any>, table: any) {
    let fromDate;
    let toDate;
    if (!table['fromDate']) {
      fromDate = null;
    } else {
      fromDate = new Date(table['fromDate']).getTime();
    }

    if (!table['toDate']) {
      toDate = null;
    } else {
      toDate = new Date(table['toDate']).getTime();
    }


    let vdsChannelCode;
    if (table['vdsChannelCode'] == '') {
      vdsChannelCode = null;
    } else {
      vdsChannelCode = table['vdsChannelCode'];
    }

    this.modelAction = new DeclareVDSModel(
      vdsChannelCode,
      table['shopCode'],
      table['shopName'],
      table['shortName'],
      table['parentShopCode'],
      table['status'],
      fromDate, toDate, null, table['id']);
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
        ignoreBackdropClick: true
      }
    );
  }

  action() {
    this.treeVDSService.cationConfigDeclareVDS(this.modelAction).subscribe(
      data => {
        // console.log(this.modelAction);
        // console.log(data);
        // this.loadData();
        this.onBack();
        this.serviceService.setReloadWarning(true);
        this.clickSearch ? this.submit() : this.loadData();
        this.toastr.success(this.modelAction['status'] == 0 ? this.translate.instant('management.declareVDS.unLockOk')
          : this.translate.instant('management.declareVDS.lockOk'),
          this.translate.instant('management.weight.infor'), {
            timeOut: 3000,
            positionClass: 'toast-top-center',
          });
      }
    );
  }

  onBack() {
    this.mobjModalRef.hide();
  }

  unitTreeData() {
    this.nodeTrees = [];
    this.nodeTreeViews = [];
    // console.log('ok');
    this.treeVDSService.getunitsVDS('').subscribe(
      next => {
        // console.log(next['data'], 'da vao data');
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
          text: pobjNodeTree.shopName,
          children: parrItems,
          checked: false
        };
      } else {
        parrItems = [];
        pobjitem = {
          value: pobjNodeTree.shopCode,
          text: pobjNodeTree.shopName,
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
          children: [],
          groupId: value.groupId,
          shortName: value.shortName
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

  check1() {
    this.declareVds.controls['child'].setValue(1);
    this.declareVds.controls['child'].valueChanges.subscribe(value => {
      this.loadData();
    });
  }

  check2() {
    this.declareVds.controls['child'].setValue(0);
    this.declareVds.controls['child'].valueChanges.subscribe(value => {
      this.loadData();
    });
  }
}
