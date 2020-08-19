package vn.vissoft.dashboard.dto;

public class Pager {
    private int mintPage;
    private int mintPageSize;
    private int mintTotalRow;
    private int minFirstPage;
    private int mintLastPage;

    public int getPage() {
        return mintPage;
    }

    public void setPage(int pintPage) {
        this.mintPage = pintPage;
    }

    public int getPageSize() {
        return mintPageSize;
    }

    public void setPageSize(int pintPageSize) {
        this.mintPageSize = pintPageSize;
    }

    public int getTotalRow() {
        return mintTotalRow;
    }

    public void setTotalRow(int pintTotalRow) {
        this.mintTotalRow = pintTotalRow;
    }

    public int getFirstPage() {
        return minFirstPage;
    }

    public void setFirstPage(int pintFirstPage) {
        this.minFirstPage = pintFirstPage;
    }

    public int getLastPage() {
        return mintLastPage;
    }

    public void setLastPage(int pintLastPage) {
        this.mintLastPage = pintLastPage;
    }
}
