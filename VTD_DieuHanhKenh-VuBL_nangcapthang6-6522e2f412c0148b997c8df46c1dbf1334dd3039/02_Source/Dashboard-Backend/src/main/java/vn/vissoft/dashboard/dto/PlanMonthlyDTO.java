package vn.vissoft.dashboard.dto;

import vn.vissoft.dashboard.helper.DataUtil;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

public class PlanMonthlyDTO extends BaseDTO {

    private Long mlngId;
    private Long mlngPrdId;
    private String shopCode;
    private Long mlngServiceId;
    private double mdblFSchedule;
    private String mstrCurrency;
    private Timestamp mdtCreateDate;
    private String mstrUser;
    private String mstrCongVan;

    private List<Long> mlstServiceIds;
    private String mstrChannelName;
    private String mstrProductName;
    private String mstrGroupServiceName;
    private String mstrServiceName;
    private Double mdblFScheduleLastMonth;
    private double mdblFValueLastMonth;
    private Double mdblCompletedLastMonth;
    private Double mdblCompareMonthAndLastMonth;
    private Long mlngPrdIdEndOfMonth;
    private String mstrGroupServiceCode;
    private String mstrProductCode;
    private String mstrMonth;
    private Long mlngGroupServiceId;
    private Long mlngProductId;
    private String mstrChannelCode;
    private Long mlngReceivedDate;
    private String mstrServiceCode;
    private String mstrStaffCode;

    private String mstrTypeSearch;
    private boolean isUpdate;

    private double mdblSumFscheduleOfParentUnit;
    private double mdblDensity;
    private String mstrStaffName;
    private String mstrUnitName;
    private List<String> shops;
    private List<String> staffs;
    private String cycleCode;

    private Long mlngReportSqlId;
    private Long mdtFromDate;
    private Long mdtToDate;

    //dat them
    private String shopName;


    public Long getId() {
        return mlngId;
    }

    public void setId(Long plngId) {
        this.mlngId = plngId;
    }

    public Long getPrdId() {
        return mlngPrdId;
    }

    public void setPrdId(Long plngPrdId) {
        this.mlngPrdId = plngPrdId;
    }


    public Long getServiceId() {
        return mlngServiceId;
    }

    public void setServiceId(Long plngServiceId) {
        this.mlngServiceId = plngServiceId;
    }

    public String getCurrency() {
        return mstrCurrency;
    }

    public void setCurrency(String pstrCurrency) {
        this.mstrCurrency = pstrCurrency;
    }

    public Timestamp getCreateDate() {
        return mdtCreateDate;
    }

    public void setCreateDate(Timestamp pdtCreateDate) {
        this.mdtCreateDate = pdtCreateDate;
    }

    public String getUser() {
        return mstrUser;
    }

    public void setUser(String pstrUser) {
        this.mstrUser = pstrUser;
    }

    public String getMonth() {
        return mstrMonth;
    }

    public void setMonth(String pstrMonth) {
        this.mstrMonth = pstrMonth;
    }

    public String getChannelName() {
        return mstrChannelName;
    }

    public void setChannelName(String pstrChannelName) {
        this.mstrChannelName = pstrChannelName;
    }

    public String getServiceName() {
        return mstrServiceName;
    }

    public void setServiceName(String pstrServiceName) {
        this.mstrServiceName = pstrServiceName;
    }

    public double getfSchedule() {
        if(DataUtil.isNullOrZero(mdblFSchedule)) {
            return 0;
        }
        return mdblFSchedule;
    }

    public void setfSchedule(double pdblFSchedule) {
        this.mdblFSchedule = pdblFSchedule;
    }

    public String getProductName() {
        return mstrProductName;
    }

    public void setProductName(String pstrProductName) {
        this.mstrProductName = pstrProductName;
    }

    public String getGroupServiceName() {
        return mstrGroupServiceName;
    }

    public void setGroupServiceName(String pstrGroupServiceName) {
        this.mstrGroupServiceName = pstrGroupServiceName;
    }

    public Double getfScheduleLastMonth() {
        return mdblFScheduleLastMonth;
    }

    public void setfScheduleLastMonth(Double pdblFScheduleLastMonth) {
        this.mdblFScheduleLastMonth = pdblFScheduleLastMonth;
    }

    public double getfValueLastMonth() {
        if(DataUtil.isNullOrZero(mdblFValueLastMonth)) {
            return 0;
        }
        return mdblFValueLastMonth;
    }

    public void setfValueLastMonth(double pdblFValueLastMonth) {
        this.mdblFValueLastMonth = pdblFValueLastMonth;
    }

    public Double getCompletedLastMonth() {
//        if(DataUtil.isNullOrZero(this.mdblFScheduleLastMonth) || DataUtil.isNullOrZero(this.mdblFValueLastMonth)) {
//            return 0;
//        }
        return mdblCompletedLastMonth;
    }

    public void setCompletedLastMonth(Double pdblCompletedLastMonth) {
        this.mdblCompletedLastMonth = pdblCompletedLastMonth;
    }

    public Double getCompareMonthAndLastMonth() {
//        if((Double.compare(mdblFScheduleLastMonth, 0) == 0 && Double.compare(mdblFSchedule, 0) != 0) ||
//                (DataUtil.isNullOrZero(mdblFScheduleLastMonth) && Double.compare(mdblFSchedule, 0) != 0)) {
//            return 0;
//        }
        return mdblCompareMonthAndLastMonth;
    }

    public void setCompareMonthAndLastMonth(Double pdblCompareMonthAndLastMonth) {
        this.mdblCompareMonthAndLastMonth = pdblCompareMonthAndLastMonth;
    }

    public Long getPrdIdEndOfMonth() {
        return mlngPrdIdEndOfMonth;
    }

    public void setPrdIdEndOfMonth(Long plngPrdIdEndOfMonth) {
        this.mlngPrdIdEndOfMonth = plngPrdIdEndOfMonth;
    }

    public String getGroupServiceCode() {
        return mstrGroupServiceCode;
    }

    public void setGroupServiceCode(String pstrGroupServiceCode) {
        this.mstrGroupServiceCode = pstrGroupServiceCode;
    }

    public String getProductCode() {
        return mstrProductCode;
    }

    public void setProductCode(String pstrProductCode) {
        this.mstrProductCode = pstrProductCode;
    }

    public List<Long> getServiceIds() {
        return mlstServiceIds;
    }

    public void setServiceIds(List<Long> plstServiceIds) {
        this.mlstServiceIds = plstServiceIds;
    }

    public String getChannelCode() {
        return mstrChannelCode;
    }

    public void setChannelCode(String pstrChannelCode) {
        this.mstrChannelCode = pstrChannelCode;
    }

    public String getServiceCode() {
        return mstrServiceCode;
    }

    public void setServiceCode(String pstrServiceCode) {
        this.mstrServiceCode = pstrServiceCode;
    }

    public String getStaffCode() {
        return mstrStaffCode;
    }

    public void setStaffCode(String pstrStaffCode) {
        this.mstrStaffCode = pstrStaffCode;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public Long getReportSqlId() {
        return mlngReportSqlId;
    }

    public void setReportSqlId(Long mlngReportSqlId) {
        this.mlngReportSqlId = mlngReportSqlId;
    }

    public Long getFromDate() {
        return mdtFromDate;
    }

    public void setFromDate(Long mdtFromDate) {
        this.mdtFromDate = mdtFromDate;
    }

    public Long getToDate() {
        return mdtToDate;
    }

    public void setToDate(Long mdtToDate) {
        this.mdtToDate = mdtToDate;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    @Override
    public boolean equals(Object pobjo) {
        if (this == pobjo) return true;
        if (pobjo == null || getClass() != pobjo.getClass()) return false;
        PlanMonthlyDTO that = (PlanMonthlyDTO) pobjo;
        return Double.compare(that.mdblFSchedule, mdblFSchedule) == 0 &&
                Double.compare(that.mdblFScheduleLastMonth, mdblFScheduleLastMonth) == 0 &&
                Double.compare(that.mdblFValueLastMonth, mdblFValueLastMonth) == 0 &&
                Double.compare(that.mdblCompletedLastMonth, mdblCompletedLastMonth) == 0 &&
                Double.compare(that.mdblCompareMonthAndLastMonth, mdblCompareMonthAndLastMonth) == 0 &&
                Double.compare(that.mdblSumFscheduleOfParentUnit, mdblSumFscheduleOfParentUnit) == 0 &&
                Double.compare(that.mdblDensity, mdblDensity) == 0 &&
                Objects.equals(mlngId, that.mlngId) &&
                Objects.equals(mlngPrdId, that.mlngPrdId) &&
                Objects.equals(shopCode, that.shopCode) &&
                Objects.equals(mlngServiceId, that.mlngServiceId) &&
                Objects.equals(mstrCurrency, that.mstrCurrency) &&
                Objects.equals(mdtCreateDate, that.mdtCreateDate) &&
                Objects.equals(mstrUser, that.mstrUser) &&
                Objects.equals(mstrCongVan, that.mstrCongVan) &&
                Objects.equals(mlstServiceIds, that.mlstServiceIds) &&
                Objects.equals(mstrChannelName, that.mstrChannelName) &&
                Objects.equals(mstrProductName, that.mstrProductName) &&
                Objects.equals(mstrGroupServiceName, that.mstrGroupServiceName) &&
                Objects.equals(mstrServiceName, that.mstrServiceName) &&
                Objects.equals(mlngPrdIdEndOfMonth, that.mlngPrdIdEndOfMonth) &&
                Objects.equals(mstrGroupServiceCode, that.mstrGroupServiceCode) &&
                Objects.equals(mstrProductCode, that.mstrProductCode) &&
                Objects.equals(mstrMonth, that.mstrMonth) &&
                Objects.equals(mlngGroupServiceId, that.mlngGroupServiceId) &&
                Objects.equals(mlngProductId, that.mlngProductId) &&
                Objects.equals(mstrChannelCode, that.mstrChannelCode) &&
                Objects.equals(mlngReceivedDate, that.mlngReceivedDate) &&
                Objects.equals(mstrServiceCode, that.mstrServiceCode) &&
                Objects.equals(mstrStaffCode, that.mstrStaffCode) &&
                Objects.equals(mstrTypeSearch, that.mstrTypeSearch) &&
                Objects.equals(mstrStaffName, that.mstrStaffName) &&
                Objects.equals(mstrUnitName, that.mstrUnitName) &&
                Objects.equals(shops, that.shops) &&
                Objects.equals(staffs, that.staffs);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public Long getGroupServiceId() {
        return mlngGroupServiceId;
    }

    public void setGroupServiceId(Long plngGroupServiceId) {
        this.mlngGroupServiceId = plngGroupServiceId;
    }

    public Long getProductId() {
        return mlngProductId;
    }

    public void setProductId(Long plngProductId) {
        this.mlngProductId = plngProductId;
    }

    public Long getReceivedDate() {
        return mlngReceivedDate;
    }

    public void setReceivedDate(Long plngReceivedDate) {
        this.mlngReceivedDate = plngReceivedDate;
    }

    public double getSumFscheduleOfParentUnit() {
        return (double) Math.round(mdblSumFscheduleOfParentUnit * 100) / 100;
    }

    public void setSumFscheduleOfParentUnit(double pdblSumFscheduleOfParentUnit) {
        this.mdblSumFscheduleOfParentUnit = pdblSumFscheduleOfParentUnit;
    }

    public double getDensity() {
        return (double) Math.round(mdblDensity * 100) / 100;
    }

    public void setDensity(double pdblDensity) {
        this.mdblDensity = pdblDensity;
    }

    public String getStaffName() {
        return mstrStaffName;
    }

    public void setStaffName(String pstrStaffName) {
        this.mstrStaffName = pstrStaffName;
    }

    public String getUnitName() {
        return mstrUnitName;
    }

    public void setUnitName(String pstrUnitName) {
        this.mstrUnitName = pstrUnitName;
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

    public String getCongVan() {
        return mstrCongVan;
    }

    public void setCongVan(String pstrCongVan) {
        this.mstrCongVan = pstrCongVan;
    }

    public String getTypeSearch() {
        return mstrTypeSearch;
    }

    public void setTypeSearch(String pstrTypeSearch) {
        this.mstrTypeSearch = pstrTypeSearch;
    }

    public String getCycleCode() {
        return cycleCode;
    }

    public void setCycleCode(String cycleCode) {
        this.cycleCode = cycleCode;
    }
}
