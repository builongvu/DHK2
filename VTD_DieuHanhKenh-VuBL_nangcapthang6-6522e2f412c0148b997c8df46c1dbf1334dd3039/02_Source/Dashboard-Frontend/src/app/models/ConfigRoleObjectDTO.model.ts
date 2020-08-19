export class ConfigRoleObjectDTO {
  id: number;
  no: number;
  roleId: number;
  objectId: number;
  status: number;
  objectCode: string;
  objectName: string;
  action: string;
  create?: boolean;
  read?: boolean;
  update?: boolean;
  delete?: boolean;
  isDefault: number;
  checkboxDefault: boolean;

  public constructor() { }
}
