package vn.vissoft.dashboard.dto.chart;

import java.util.List;

public class TableDTO extends ContentBaseDTO {
    private List<TableColumnDTO> mlstColumns;
    private List<TableRowDTO> mlstRows;
    private String mstrTitle;
    private String mstrSubTitle;
    private String titleColor;
    private boolean mblnDownloadDetail;
    private int mintTotal;
    private int mintPageNum;
    private int mintPageSize;
    private boolean allStaff;

    public List<TableColumnDTO> getColumns() {
        return mlstColumns;
    }

    public void setColumns(List<TableColumnDTO> plstColumns) {
        this.mlstColumns = plstColumns;
    }

    public String getTitle() {
        return mstrTitle;
    }

    public void setTitle(String pstrTitle) {
        this.mstrTitle = pstrTitle;
    }

    public String getSubTitle() {
        return mstrSubTitle;
    }

    public void setSubTitle(String pstrSubTitle) {
        this.mstrSubTitle = pstrSubTitle;
    }

    public List<TableRowDTO> getRows() {
        return mlstRows;
    }

    public void setRows(List<TableRowDTO> plstRows) {
        this.mlstRows = plstRows;
    }

    public int getTotal() {
        return mintTotal;
    }

    public void setTotal(int pintTotal) {
        this.mintTotal = pintTotal;
    }

    public int getPageNum() {
        return mintPageNum;
    }

    public void setPageNum(int pintPageNum) {
        this.mintPageNum = pintPageNum;
    }

    public int getPageSize() {
        return mintPageSize;
    }

    public void setPageSize(int pintPageSize) {
        this.mintPageSize = pintPageSize;
    }

    public String getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(String titleColor) {
        this.titleColor = titleColor;
    }

    public boolean isDownloadDetail() {
        return mblnDownloadDetail;
    }

    public void setDownloadDetail(boolean mblnDownloadDetail) {
        this.mblnDownloadDetail = mblnDownloadDetail;
    }

    public boolean isAllStaff() {
        return allStaff;
    }

    public void setAllStaff(boolean allStaff) {
        this.allStaff = allStaff;
    }
}
