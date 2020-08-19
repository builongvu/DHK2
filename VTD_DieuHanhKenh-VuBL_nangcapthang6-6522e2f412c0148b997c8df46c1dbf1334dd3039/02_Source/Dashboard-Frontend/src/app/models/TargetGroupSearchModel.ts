import {Pager} from './Pager';
import { SortTable } from './SortTable';

export class TargetGroupSearchModel {    
  ProductId: number;
  Id:number;
  Code: string;
  Name: string;
  createdate:string;
  Status:string;
  pager?: Pager;
  sort?: SortTable;

  // constructor(page: Pager) {
  //   this.pager = page;
  // }  
  
  constructor(ProductId: number, Code: string, Name: string, pager?: Pager, sort?: SortTable) {    
    this.ProductId = ProductId;
    this.Code = Code;
    this.Name = Name;
    if(pager){
      this.pager = pager;
    }
    if(sort){
      this.sort = sort;
    }
    
  }
}
