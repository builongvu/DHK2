package vn.vissoft.dashboard.dto;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class PlanYearlyDTO extends BaseDTO {

    private Long id;
    private Long prdId;
    private Long serviceId;
    private String vdsChannelCode;
    private String shopCode;
    private String staffCode;
    private Double fSchedule;
    private String currency;
    private String user;
    private Timestamp createDate;
    private String congVan;

    private List<Long> mlstServiceIds;
    private String mstrChannelName;
    private String mstrProductName;
    private String mstrGroupServiceName;
    private String mstrServiceName;
    private double mdblFScheduleLastMonth;
    private double mdblFValueLastMonth;
    private double mdblCompletedLastMonth;
    private double mdblCompareMonthAndLastMonth;
    private Long mlngPrdIdEndOfMonth;
    private String mstrGroupServiceCode;
    private String mstrProductCode;
    private String mstrMonth;
    private String year;
    private Long mlngGroupServiceId;
    private Long mlngProductId;
    private String mstrChannelCode;
    private Long mlngReceivedDate;
    private String mstrServiceCode;
    private String mstrStaffCode;
    private String cycleCode;

    private String mstrTypeSearch;
    private boolean isUpdate;

    private double mdblSumFscheduleOfParentUnit;
    private double mdblDensity;
    private String mstrStaffName;
    private String mstrUnitName;
    private List<String> shops;
    private List<String> staffs;

    //dat them
    private String shopName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrdId() {
        return prdId;
    }

    public void setPrdId(Long prdId) {
        this.prdId = prdId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getVdsChannelCode() {
        return vdsChannelCode;
    }

    public void setVdsChannelCode(String vdsChannelCode) {
        this.vdsChannelCode = vdsChannelCode;
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

    public Double getfSchedule() {
        return fSchedule;
    }

    public void setfSchedule(Double fSchedule) {
        this.fSchedule = fSchedule;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public String getCongVan() {
        return congVan;
    }

    public void setCongVan(String congVan) {
        this.congVan = congVan;
    }

    public List<Long> getMlstServiceIds() {
        return mlstServiceIds;
    }

    public void setMlstServiceIds(List<Long> mlstServiceIds) {
        this.mlstServiceIds = mlstServiceIds;
    }

    public String getChannelName() {
        return mstrChannelName;
    }

    public void setChannelName(String mstrChannelName) {
        this.mstrChannelName = mstrChannelName;
    }

    public String getProductName() {
        return mstrProductName;
    }

    public void setProductName(String mstrProductName) {
        this.mstrProductName = mstrProductName;
    }

    public String getGroupServiceName() {
        return mstrGroupServiceName;
    }

    public void setGroupServiceName(String mstrGroupServiceName) {
        this.mstrGroupServiceName = mstrGroupServiceName;
    }

    public String getServiceName() {
        return mstrServiceName;
    }

    public void setServiceName(String mstrServiceName) {
        this.mstrServiceName = mstrServiceName;
    }

    public double getfScheduleLastMonth() {
        return mdblFScheduleLastMonth;
    }

    public void setfScheduleLastMonth(double mdblFScheduleLastMonth) {
        this.mdblFScheduleLastMonth = mdblFScheduleLastMonth;
    }

    public double getfValueLastMonth() {
        return mdblFValueLastMonth;
    }

    public void setfValueLastMonth(double mdblFValueLastMonth) {
        this.mdblFValueLastMonth = mdblFValueLastMonth;
    }

    public double getCompletedLastMonth() {
        return mdblCompletedLastMonth;
    }

    public void setCompletedLastMonth(double mdblCompletedLastMonth) {
        this.mdblCompletedLastMonth = mdblCompletedLastMonth;
    }

    public double getCompareMonthAndLastMonth() {
        return mdblCompareMonthAndLastMonth;
    }

    public void setCompareMonthAndLastMonth(double mdblCompareMonthAndLastMonth) {
        this.mdblCompareMonthAndLastMonth = mdblCompareMonthAndLastMonth;
    }

    public Long getPrdIdEndOfMonth() {
        return mlngPrdIdEndOfMonth;
    }

    public void setPrdIdEndOfMonth(Long mlngPrdIdEndOfMonth) {
        this.mlngPrdIdEndOfMonth = mlngPrdIdEndOfMonth;
    }

    public String getMstrGroupServiceCode() {
        return mstrGroupServiceCode;
    }

    public void setMstrGroupServiceCode(String mstrGroupServiceCode) {
        this.mstrGroupServiceCode = mstrGroupServiceCode;
    }

    public String getMstrProductCode() {
        return mstrProductCode;
    }

    public void setMstrProductCode(String mstrProductCode) {
        this.mstrProductCode = mstrProductCode;
    }

    public String getMonth() {
        return mstrMonth;
    }

    public void setMonth(String mstrMonth) {
        this.mstrMonth = mstrMonth;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Long getMlngGroupServiceId() {
        return mlngGroupServiceId;
    }

    public void setMlngGroupServiceId(Long mlngGroupServiceId) {
        this.mlngGroupServiceId = mlngGroupServiceId;
    }

    public Long getMlngProductId() {
        return mlngProductId;
    }

    public void setMlngProductId(Long mlngProductId) {
        this.mlngProductId = mlngProductId;
    }

    public String getChannelCode() {
        return mstrChannelCode;
    }

    public void setChannelCode(String mstrChannelCode) {
        this.mstrChannelCode = mstrChannelCode;
    }

    public Long getMlngReceivedDate() {
        return mlngReceivedDate;
    }

    public void setMlngReceivedDate(Long mlngReceivedDate) {
        this.mlngReceivedDate = mlngReceivedDate;
    }

    public String getServiceCode() {
        return mstrServiceCode;
    }

    public void setServiceCode(String mstrServiceCode) {
        this.mstrServiceCode = mstrServiceCode;
    }

    public String getMstrStaffCode() {
        return mstrStaffCode;
    }

    public void setMstrStaffCode(String mstrStaffCode) {
        this.mstrStaffCode = mstrStaffCode;
    }

    public String getMstrTypeSearch() {
        return mstrTypeSearch;
    }

    public void setMstrTypeSearch(String mstrTypeSearch) {
        this.mstrTypeSearch = mstrTypeSearch;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public double getMdblSumFscheduleOfParentUnit() {
        return mdblSumFscheduleOfParentUnit;
    }

    public void setMdblSumFscheduleOfParentUnit(double mdblSumFscheduleOfParentUnit) {
        this.mdblSumFscheduleOfParentUnit = mdblSumFscheduleOfParentUnit;
    }

    public double getDensity() {
        return mdblDensity;
    }

    public void setDensity(double mdblDensity) {
        this.mdblDensity = mdblDensity;
    }

    public String getStaffName() {
        return mstrStaffName;
    }

    public void setStaffName(String mstrStaffName) {
        this.mstrStaffName = mstrStaffName;
    }

    public String getUnitName() {
        return mstrUnitName;
    }

    public void setUnitName(String mstrUnitName) {
        this.mstrUnitName = mstrUnitName;
    }

    public List<String> getShops() {
        return shops;
    }

    public void setShops(List<String> shops) {
        this.shops = shops;
    }

    public List<String> getStaffs() {
        return staffs;
    }

    public void setStaffs(List<String> staffs) {
        this.staffs = staffs;
    }

    public String getCycleCode() {
        return cycleCode;
    }

    public void setCycleCode(String cycleCode) {
        this.cycleCode = cycleCode;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
