package vn.vissoft.dashboard.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "PLAN_MONTHLY")
public class PlanMonthly {

    private Long mlngId;
    private Long mlngPrdId;
    private String vdsChannelCode;
    private Long mlngServiceId;
    private String staffCode;
    private double mdblFSchedule;
    private String mstrCurrency;
    private Timestamp mdtCreateDate;
    private String mstrUser;
    private String shopCode;

    public PlanMonthly() {
    }

    public PlanMonthly(Long mlngPrdId, String channelCode, Long serviceId, String shopCodeVDS, Double schedule, String user, String staffCode,Timestamp createDate) {
        this.mlngPrdId = mlngPrdId;
        this.vdsChannelCode = channelCode;
        this.mlngServiceId = serviceId;
        this.shopCode = shopCodeVDS;
        this.mdblFSchedule = schedule;
        this.mstrUser = user;
        this.staffCode = staffCode;
        this.mdtCreateDate=createDate;

    }

    public PlanMonthly(Long mlngPrdId,String channelCode, Long serviceIdByCode, String staffCode, String shopCodeVDS, Double schedule, String user,Timestamp createDate) {
        this.mlngPrdId = mlngPrdId;
        this.vdsChannelCode = channelCode;
        this.mlngServiceId = serviceIdByCode;
        this.shopCode = shopCodeVDS;
        this.mdblFSchedule = schedule;
        this.staffCode = staffCode;
        this.mstrUser = user;
        this.mdtCreateDate=createDate;
    }
    public PlanMonthly(Long id,Long mlngPrdId,String channelCode, Long serviceIdByCode, String staffCode, String shopCodeVDS, Double schedule, String user,Timestamp createDate) {
        this.mlngId=id;
        this.mlngPrdId = mlngPrdId;
        this.vdsChannelCode = channelCode;
        this.mlngServiceId = serviceIdByCode;
        this.shopCode = shopCodeVDS;
        this.mdblFSchedule = schedule;
        this.staffCode = staffCode;
        this.mstrUser = user;
        this.mdtCreateDate=createDate;
    }

    @Column(name = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(name = "F_SCHEDULE")
    public double getfSchedule() {
        return mdblFSchedule;
    }

    public void setfSchedule(double pdblFSchedule) {
        this.mdblFSchedule = pdblFSchedule;
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
    public Timestamp getCreateDate() {
        return mdtCreateDate;
    }

    public void setCreateDate(Timestamp pdtCreateDate) {
        this.mdtCreateDate = pdtCreateDate;
    }

    @Basic
    @Column(name = "USER")
    public String getUser() {
        return mstrUser;
    }

    public void setUser(String pstrUser) {
        this.mstrUser = pstrUser;
    }

    @Basic
    @Column(name = "vds_channel_code")
    public String getVdsChannelCode() {
        return vdsChannelCode;
    }

    public void setVdsChannelCode(String vdsChannelCode) {
        this.vdsChannelCode = vdsChannelCode;
    }

    @Basic
    @Column(name = "staff_code")
    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    @Basic
    @Column(name = "shop_code")
    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

}
