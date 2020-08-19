package vn.vissoft.dashboard.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "STAFF")
public class Staff {

    private Long mlngId;
    private String mstrCode;
    private String mstrName;
    private Long shopId;
    private String ttnsCode;
    private Long staffOwnerId;
    private String status;

    @Column(name = "STAFF_ID")
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
    @Column(name = "Staff_code")
    public String getStaffCode() {
        return mstrCode;
    }

    public void setStaffCode(String pstrCode) {
        this.mstrCode = pstrCode;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
        return mstrName;
    }

    public void setName(String pstrName) {
        this.mstrName = pstrName;
    }

    @Column(name = "shop_id")
    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    @Column(name = "ttns_code")
    public String getTtnsCode() {
        return ttnsCode;
    }

    public void setTtnsCode(String ttnsCode) {
        this.ttnsCode = ttnsCode;
    }

    @Column(name = "staff_owner_id")
    public Long getStaffOwnerId() {
        return staffOwnerId;
    }

    public void setStaffOwnerId(Long staffOwnerId) {
        this.staffOwnerId = staffOwnerId;
    }

    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
