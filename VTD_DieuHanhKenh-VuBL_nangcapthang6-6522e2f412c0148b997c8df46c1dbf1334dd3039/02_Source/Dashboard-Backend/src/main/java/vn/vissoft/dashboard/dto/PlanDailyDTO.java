package vn.vissoft.dashboard.dto;

import java.sql.Date;

public class PlanDailyDTO {

    private Long mlngId;
    private Long mlngPrdId;
    private Long mlngChannelId;
    private Long mlngServiceId;
    private Long mlngUnitId;
    private Long mlngStaffId;
    private double mdblFschedule;
    private double mdblFscheduleMonth;
    private String mstrCurrency;
    private Date mdtCreateDatetime;

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

    public Long getChannelId() {
        return mlngChannelId;
    }

    public void setChannelId(Long plngChannelId) {
        this.mlngChannelId = plngChannelId;
    }

    public Long getUnitId() {
        return mlngUnitId;
    }

    public void setUnitId(Long plngUnitId) {
        this.mlngUnitId = plngUnitId;
    }

    public Long getStaffId() {
        return mlngStaffId;
    }

    public void setStaffId(Long plngStaffId) {
        this.mlngStaffId = plngStaffId;
    }

    public double getfSchedule() {
        return mdblFschedule;
    }

    public void setfSchedule(double pdblFschedule) {
        this.mdblFschedule = pdblFschedule;
    }

    public double getfScheduleMonth() {
        return mdblFscheduleMonth;
    }

    public void setfScheduleMonth(double pdblFscheduleMonth) {
        this.mdblFscheduleMonth = pdblFscheduleMonth;
    }

    public String getCurrency() {
        return mstrCurrency;
    }

    public void setCurrency(String pstrCurrency) {
        this.mstrCurrency = pstrCurrency;
    }

    public Date getCreateDatetime() {
        return mdtCreateDatetime;
    }

    public void setCreateDatetime(Date pdtCreateDatetime) {
        this.mdtCreateDatetime = pdtCreateDatetime;
    }

    public Long getServiceId() {
        return mlngServiceId;
    }

    public void setServiceId(Long plngServiceId) {
        this.mlngServiceId = plngServiceId;
    }
}
