export class CellModel {
  bold: false;
  color: '#000000';
  columnId: 0;
  viewValue: string;


  constructor(viewValue: string) {
    this.viewValue = viewValue;
  }
}
