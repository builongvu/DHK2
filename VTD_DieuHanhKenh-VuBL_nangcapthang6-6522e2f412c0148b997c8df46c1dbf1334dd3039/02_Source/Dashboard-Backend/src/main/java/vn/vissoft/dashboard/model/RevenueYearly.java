package vn.vissoft.dashboard.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;

@Entity
@Table(name = "REVENUE_YEAR")
public class RevenueYearly {

    private Long mlngId;
    private Long mlngPrdId;
    private Long mlngServiceId;
    private double mdblFValue;
    private String mstrCurrency;
    private Date mdtCreateDatetime;
    private String mstrUser;
    private String vdsChannelCode;
    private String shopCode;
    private String staffCode;
    @Column(name = "ID")
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return mlngId;
    }

    public void setId(Long plngId) {
        this.mlngId = plngId;
    }

    @Basic
    @Column(name = "PRD_ID")
    public Long getPrdId() {
        return mlngPrdId;
    }

    public void setPrdId(Long plngPrdId) {
        this.mlngPrdId = plngPrdId;
    }

    @Basic
    @Column(name = "SERVICE_ID")
    public Long getServiceId() {
        return mlngServiceId;
    }

    public void setServiceId(Long plngServiceId) {
        this.mlngServiceId = plngServiceId;
    }

    @Basic
    @Column(name = "F_VALUE")
    public double getfValue() {
        return mdblFValue;
    }

    public void setfValue(double pdblFValue) {
        this.mdblFValue = pdblFValue;
    }

    @Basic
    @Column(name = "CURRENCY")
    public String getCurrency() {
        return mstrCurrency;
    }

    public void setCurrency(String pstrCurrency) {
        this.mstrCurrency = pstrCurrency;
    }

    @Basic
    @Column(name = "CREATE_DATE")
    public Date getCreateDatetime() {
        return mdtCreateDatetime;
    }

    public void setCreateDatetime(Date pdtCreateDatetime) {
        this.mdtCreateDatetime = pdtCreateDatetime;
    }

    @Basic
    @Column(name = "USER")
    public String getUser() {
        return mstrUser;
    }

    public void setUser(String pstrUser) {
        this.mstrUser = pstrUser;
    }


    @Column(name = "vds_channel_code")
    public String getVdsChannelCode() {
        return vdsChannelCode;
    }

    public void setVdsChannelCode(String vdsChannelCode) {
        this.vdsChannelCode = vdsChannelCode;
    }

    @Column(name = "shop_code")
    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    @Column(name = "staff_code")
    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }
}

