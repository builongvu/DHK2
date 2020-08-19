export class GroupModel {
  id?: number;
  code?: string;
  name?: string;
  status?: string;
  productId?: Number;
  createdate?: string;
  productName?: string;
  changeDatetime?: Date;
  stringDate?: string;
  userUpdate: string;
  ChangeDatetime?: string;
  changeDatetime1?: string;
  cCheckBox: boolean;

  // constructor(){
  //
  // }

  constructor(code?: string, name?: string, productId?: number, status?: string) {
    this.code = code;
    this.name = name;
    this.productId = productId;
    this.status = status;
  }
}

export class Searchgroup {
  productId: number;
  code: string;
  name: string;

  constructor() {

  }
}
