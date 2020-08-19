package vn.vissoft.dashboard.model;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "PLAN_YEARLY")
public class PlanYearly {

    private Long mlngId;
    private Long mlngPrdId;
    private String vdsChannelCode;
    private Long mlngServiceId;
    private String staffCode;
    private double mdblFSchedule;
    private String mstrCurrency;
    private Timestamp mdtCreateDatetime;
    private String shopCode;
    private String user;

    public PlanYearly() {
    }

    public PlanYearly(Long mlngPrdId, String channelCode, Long serviceId, String shopCodeVDS, Double schedule, String user, String staffCode, Timestamp createDate) {
        this.mlngPrdId = mlngPrdId;
        this.vdsChannelCode = channelCode;
        this.mlngServiceId = serviceId;
        this.shopCode = shopCodeVDS;
        this.mdblFSchedule = schedule;
        this.user = user;
        this.staffCode = staffCode;
        this.mdtCreateDatetime = createDate;

    }

    public PlanYearly(Long mlngPrdId, String channelCode, Long serviceIdByCode, String staffCode, String shopCodeVDS, Double schedule, String user, Timestamp createDate) {
        this.mlngPrdId = mlngPrdId;
        this.vdsChannelCode = channelCode;
        this.mlngServiceId = serviceIdByCode;
        this.shopCode = shopCodeVDS;
        this.mdblFSchedule = schedule;
        this.staffCode = staffCode;
        this.user = user;
        this.mdtCreateDatetime = createDate;
    }
    public PlanYearly(Long id,Long mlngPrdId, String channelCode, Long serviceIdByCode, String staffCode, String shopCodeVDS, Double schedule, String user, Timestamp createDate) {
        this.mlngId=id;
        this.mlngPrdId = mlngPrdId;
        this.vdsChannelCode = channelCode;
        this.mlngServiceId = serviceIdByCode;
        this.shopCode = shopCodeVDS;
        this.mdblFSchedule = schedule;
        this.staffCode = staffCode;
        this.user = user;
        this.mdtCreateDatetime = createDate;
    }

    @Id
    @Column(name = "ID")
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
    public Timestamp getCreateDatetime() {
        return mdtCreateDatetime;
    }

    public void setCreateDatetime(Timestamp pdtCreateDatetime) {
        this.mdtCreateDatetime = pdtCreateDatetime;
    }

    @Column(name = "vds_channel_code")
    public String getVdsChannelCode() {
        return vdsChannelCode;
    }

    public void setVdsChannelCode(String vdsChannelCode) {
        this.vdsChannelCode = vdsChannelCode;
    }

    @Column(name = "staff_code")
    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    @Column(name = "shop_code")
    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    @Basic
    @Column(name = "USER")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
