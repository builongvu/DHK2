package vn.vissoft.dashboard.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "PLAN_QUARTERLY")
public class PlanQuarterly {

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

    public PlanQuarterly() {
    }

    public PlanQuarterly(Long mlngPrdId, String channelCode, Long serviceId, String shopCodeVDS, Double schedule, String user, String staffCode,Timestamp createDate) {
        this.prdId = mlngPrdId;
        this.vdsChannelCode = channelCode;
        this.serviceId = serviceId;
        this.shopCode = shopCodeVDS;
        this.fSchedule = schedule;
        this.user = user;
        this.staffCode = staffCode;
        this.createDate=createDate;

    }
    public PlanQuarterly(Long mlngPrdId,String channelCode, Long serviceIdByCode, String staffCode, String shopCodeVDS, Double schedule, String user,Timestamp createDate) {
        this.prdId = mlngPrdId;
        this.vdsChannelCode = channelCode;
        this.serviceId = serviceIdByCode;
        this.shopCode = shopCodeVDS;
        this.fSchedule = schedule;
        this.staffCode = staffCode;
        this.user = user;
        this.createDate=createDate;
    }
    public PlanQuarterly(Long id,Long mlngPrdId,String channelCode, Long serviceIdByCode, String staffCode, String shopCodeVDS, Double schedule, String user,Timestamp createDate) {
        this.id=id;
        this.prdId = mlngPrdId;
        this.vdsChannelCode = channelCode;
        this.serviceId = serviceIdByCode;
        this.shopCode = shopCodeVDS;
        this.fSchedule = schedule;
        this.staffCode = staffCode;
        this.user = user;
        this.createDate=createDate;
    }

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
    @Column(name = "VDS_CHANNEL_CODE")
    public String getVdsChannelCode() {
        return vdsChannelCode;
    }

    public void setVdsChannelCode(String vdsChannelCode) {
        this.vdsChannelCode = vdsChannelCode;
    }

    @Basic
    @Column(name = "SHOP_CODE")
    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    @Basic
    @Column(name = "STAFF_CODE")
    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    @Basic
    @Column(name = "F_SCHEDULE")
    public Double getfSchedule() {
        return fSchedule;
    }

    public void setfSchedule(Double fSchedule) {
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
    @Column(name = "USER")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Basic
    @Column(name = "CREATE_DATE")
    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
