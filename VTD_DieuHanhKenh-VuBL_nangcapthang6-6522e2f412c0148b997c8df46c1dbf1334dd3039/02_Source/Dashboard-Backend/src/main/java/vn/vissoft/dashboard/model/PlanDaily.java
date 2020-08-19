package vn.vissoft.dashboard.model;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "PLAN_DAILY")
public class PlanDaily {

    private Long mlngId;
    private Long mlngPrdId;
    private String vdsChannelCode;
    private Long mlngServiceId;
    private String staffCode;
    private double mdblFschedule;
    private double mdblFscheduleMonth;
    private String mstrCurrency;
    private Date mdtCreateDatetime;
    private String shopCode;

    @Id
    @Column(name = "id")
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
        return mdblFschedule;
    }

    public void setfSchedule(double pdblFschedule) {
        this.mdblFschedule = pdblFschedule;
    }

    @Basic
    @Column(name = "F_SCHEDULE_MONTH")
    public double getfScheduleMonth() {
        return mdblFscheduleMonth;
    }

    public void setfScheduleMonth(double pdblFscheduleMonth) {
        this.mdblFscheduleMonth = pdblFscheduleMonth;
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
}
