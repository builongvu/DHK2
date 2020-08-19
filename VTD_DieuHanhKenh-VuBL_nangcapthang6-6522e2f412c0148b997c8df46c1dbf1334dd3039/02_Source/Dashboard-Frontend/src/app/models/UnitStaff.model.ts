export class UnitStaffModel {
  objectType: string;
  objectName: string;
  parentId: number;
  text: string;
  value: string;
  objectId: number;
  children: UnitStaffModel[];

  constructor() {
  }
}
