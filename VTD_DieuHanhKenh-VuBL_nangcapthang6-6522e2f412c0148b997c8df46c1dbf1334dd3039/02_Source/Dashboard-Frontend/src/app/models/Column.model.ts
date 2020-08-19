export class ColumnModel {
  align: 'left' | 'right' | 'center';
  name: string;


  constructor(align: 'left' | 'right' | 'center', name: string) {
    this.align = align;
    this.name = name;
  }
}
