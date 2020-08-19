export class Pager {
    page: number;
    pageSize: number;
    totalRow: number;
    firstPage: number;
    lastPage: number;

  // @ts-ignore
  public constructor(page, pageSize) {
    this.page = page;
    this.pageSize = pageSize;
  }

    // @ts-ignore
  public constructor() {
        this.page = 1;
        this.pageSize = 5;
    }
}
