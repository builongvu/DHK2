package vn.vissoft.dashboard.dto;

public class DashboardRequestDTO {

    private Long mlngGroupId;
    private Long mlngPrdId;
    private String mstrObjectCode;
    private String vdsChannelCode;
    private int mintCycleId;
    private Long serviceId;
    private String parentShopCode;
    private int isTarget;
    private int mintExpand;
    private Long chartId;
    private String sorted;
    //test
    private String serviceIds;
    private String codeWarning;
    private String pRow;
    private String nationalStaff;
    private String month;
    private String monthYear;

    public Long getGroupId() {
        return mlngGroupId;
    }

    public void setGroupId(Long plngGroupId) {
        this.mlngGroupId = plngGroupId;
    }

    public Long getPrdId() {
        return mlngPrdId;
    }

    public void setPrdId(Long plngPrdId) {
        this.mlngPrdId = plngPrdId;
    }

    public String getObjectCode() {
        return mstrObjectCode;
    }

    public void setObjectCode(String objectCode) {
        mstrObjectCode = objectCode;
    }

    public String getVdsChannelCode() {
        return vdsChannelCode;
    }

    public void setVdsChannelCode(String vdsChannelCode) {
        this.vdsChannelCode = vdsChannelCode;
    }

    public int getCycleId() {
        return mintCycleId;
    }

    public void setCycleId(int pintCycleId) {
        this.mintCycleId = pintCycleId;
    }

    public String getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(String serviceIds) {
        this.serviceIds = serviceIds;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getParentShopCode() {
        return parentShopCode;
    }

    public void setParentShopCode(String parentShopCode) {
        this.parentShopCode = parentShopCode;
    }

    public int getIsTarget() {
        return isTarget;
    }

    public void setIsTarget(int isTarget) {
        this.isTarget = isTarget;
    }

    public int getExpand() {
        return mintExpand;
    }

    public void setExpand(int mintExpand) {
        this.mintExpand = mintExpand;
    }

    public Long getChartId() {
        return chartId;
    }

    public void setChartId(Long chartId) {
        this.chartId = chartId;
    }

    public String getSorted() {
        return sorted;
    }

    public void setSorted(String sorted) {
        this.sorted = sorted;
    }

    public String getCodeWarning() {
        return codeWarning;
    }

    public void setCodeWarning(String codeWarning) {
        this.codeWarning = codeWarning;
    }

    public String getpRow() {
        return pRow;
    }

    public void setpRow(String pRow) {
        this.pRow = pRow;
    }

    public String getNationalStaff() {
        return nationalStaff;
    }

    public void setNationalStaff(String nationalStaff) {
        this.nationalStaff = nationalStaff;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }
}
