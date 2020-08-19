export class ReportFilterSqlDTO {
  month: string;
  user: string;
  type: string;

  constructor(month,user,type) {
    this.month = month;
    this.user = user;
    this.type = type;
  }
}
