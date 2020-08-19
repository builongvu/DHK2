import { Pager } from './Pager';

export class DVT {

  id: number;
  code: string;
  name: string;
  rate: number;
  status: number;
  check: boolean;
  constructor() {}

}

export class DVTDTO {
  code: string;
  name: string
  rate: number;
  status: number;
  pager: Pager;

  constructor() {}

}
