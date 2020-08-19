package vn.vissoft.dashboard.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "manage_info_partner")
public class  ManageInfoPartner {

    private Long id;
    private String vdsChannelCode;
    private String shopCode;
    private String shopName;
    private String parentShopCode;
    private String assignKpi;
    private String status;
    private String user;
    private Date createDate;
    private String shortName;
    private Date fromDate;
    private Date toDate;

    @Column(name = "id")
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Column(name = "shop_name")
    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    @Column(name = "parent_shop_code")
    public String getParentShopCode() {
        return parentShopCode;
    }

    public void setParentShopCode(String parentShopCode) {
        this.parentShopCode = parentShopCode;
    }

    @Column(name = "assign_kpi")
    public String getAssignKpi() {
        return assignKpi;
    }

    public void setAssignKpi(String assignKpi) {
        this.assignKpi = assignKpi;
    }

    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "user")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Column(name = "create_date")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "short_name")
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Column(name = "from_date")
    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    @Column(name = "to_date")
    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
}
