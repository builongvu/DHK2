package vn.vissoft.dashboard.dto;

public class SqlMonthlyReportDTO {

    private Long fromDate;
    private Long toDate;
    private String shopCode;
    private Long idReport;

    public Long getFromDate() {
        return fromDate;
    }

    public void setFromDate(Long fromDate) {
        this.fromDate = fromDate;
    }

    public Long getToDate() {
        return toDate;
    }

    public void setToDate(Long toDate) {
        this.toDate = toDate;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public Long getIdReport() {
        return idReport;
    }

    public void setIdReport(Long idReport) {
        this.idReport = idReport;
    }
}
