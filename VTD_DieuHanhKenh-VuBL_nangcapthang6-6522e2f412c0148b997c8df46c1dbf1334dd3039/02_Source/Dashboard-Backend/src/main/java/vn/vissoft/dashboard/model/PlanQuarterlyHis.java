package vn.vissoft.dashboard.model;

import java.sql.Date;
import java.sql.Timestamp;
import javax.persistence.*;

/**
 * Created by PC on 06/12/2019 15:34
 */

@Entity
@Table(name="plan_quarterly_his")
public class PlanQuarterlyHis {

    private Long id;
    private Long prdId;
    private String vdsChannelCode;
    private Long serviceId;
    private String staffCode;
    private double fSchedule;
    private String currency;
    private Timestamp createDate;
    private String user;
    private String shopCode;
    private Date orgDatetime;

    @Column(name = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "PRD_ID")
    public Long getPrdId() {
        return prdId;
    }

    public void setPrdId(Long prdId) {
        this.prdId = prdId;
    }

    @Basic
    @Column(name = "SERVICE_ID")
    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
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
    @Column(name = "F_SCHEDULE")
    public double getfSchedule() {
        return fSchedule;
    }

    public void setfSchedule(double fSchedule) {
        this.fSchedule = fSchedule;
    }

    @Basic
    @Column(name = "CURRENCY")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Basic
    @Column(name = "CREATE_DATE")
    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    @Basic
    @Column(name = "USER")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

    @Basic
    @Column(name = "org_datetime")
    public Date getOrgDatetime() {
        return orgDatetime;
    }

    public void setOrgDatetime(Date orgDatetime) {
        this.orgDatetime = orgDatetime;
    }
}
