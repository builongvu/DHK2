export interface ItemTreeModel {
  objectId: number;
  objectType: string;
  objectName: string;
  parentId: number;
  children?: ItemTreeModel[];
}
