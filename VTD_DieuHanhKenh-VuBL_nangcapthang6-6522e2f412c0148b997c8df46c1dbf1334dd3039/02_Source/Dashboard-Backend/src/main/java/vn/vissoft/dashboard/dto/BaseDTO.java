package vn.vissoft.dashboard.dto;

public class BaseDTO {
    private Pager pager;
    private Sort sort;

    public Pager getPager() {
        return pager;
    }

    public void setPager(Pager pager) {
        this.pager = pager;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }
}
