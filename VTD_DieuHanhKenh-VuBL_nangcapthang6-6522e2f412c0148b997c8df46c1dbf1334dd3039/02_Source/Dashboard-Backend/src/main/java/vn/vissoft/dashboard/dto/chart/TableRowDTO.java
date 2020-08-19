package vn.vissoft.dashboard.dto.chart;

import java.util.List;

public class TableRowDTO {
    private int mintRowIndex;
    private List<TableCellBaseDTO> mlstCells;
    private Long serviceId;
    private String shopCode;
    private boolean isClicked;
    private String staffCode;

    public TableRowDTO() {
    }

    public TableRowDTO(int mintRowIndex, List<TableCellBaseDTO> mlstCells, Long serviceId, String shopCode, boolean isClicked, String staffCode) {
        this.mintRowIndex = mintRowIndex;
        this.mlstCells = mlstCells;
        this.serviceId = serviceId;
        this.shopCode = shopCode;
        this.isClicked = isClicked;
        this.staffCode = staffCode;
    }

    public int getRowIndex() {
        return mintRowIndex;
    }

    public void setRowIndex(int pintRowIndex) {
        this.mintRowIndex = pintRowIndex;
    }

    public List<TableCellBaseDTO> getCells() {
        return mlstCells;
    }

    public void setCells(List<TableCellBaseDTO> plstCells) {
        this.mlstCells = plstCells;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }
}
