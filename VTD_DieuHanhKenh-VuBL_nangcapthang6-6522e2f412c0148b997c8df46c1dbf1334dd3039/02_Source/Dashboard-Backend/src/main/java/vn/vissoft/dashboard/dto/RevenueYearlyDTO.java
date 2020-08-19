package vn.vissoft.dashboard.dto;

import java.sql.Date;

public class RevenueYearlyDTO {

    private Long mlngId;
    private Long mlngPrdId;
    private Long mlngChannelId;
    private Long mlngUnitId;
    private Long mlngStaffId;
    private double mdblFValue;
    private String mstrCurrency;
    private Date mdtCreateDatetime;
    private String mstrUser;

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

    public double getfValue() {
        return mdblFValue;
    }

    public void setfValue(double pdblFValue) {
        this.mdblFValue = pdblFValue;
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

    public String getUser() {
        return mstrUser;
    }

    public void setUser(String pstrUser) {
        this.mstrUser = pstrUser;
    }
}

