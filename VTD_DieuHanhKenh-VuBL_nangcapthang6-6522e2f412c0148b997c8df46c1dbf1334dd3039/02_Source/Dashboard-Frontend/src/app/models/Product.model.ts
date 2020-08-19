export class ProductModel {
  mlngId: number;
  mstrCode: string;
  mstrName: string;
  mstrStatus: number;
  // userUpdate: string;
  // changeDatetime: string;
  
  public constructor(){
        
  }
}
export class ProductTree {
  mlngId: number;
  mstrCode: string;
  mstrName: string;
  mstrStatus: number;
  children: ProductTree[];
  // userUpdate: string;
  // changeDatetime: string;
  
  public constructor(){
        
  }
}
