package vn.vissoft.dashboard.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ACTION_AUDIT")
public class ActionAudit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Basic
    private Long id;

    @Column(name = "ACTION_CODE")
    @Basic
    private String actionCode;

    @Column(name = "ACTION_DATETIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Basic
    private Date actionDateTime;

    @Column(name = "OBJECT_CODE")
    @Basic
    private String objectCode;

    @Column(name = "USER")
    @Basic
    private String user;

    @Basic
    @Column(name = "PK_ID")
    private Long pkID;
    @Basic
    @Column(name = "SHOP_CODE")
    private String shopCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public Date getActionDateTime() {
        return actionDateTime;
    }

    public void setActionDateTime(Date actionDateTime) {
        this.actionDateTime = actionDateTime;
    }

    public String getObjectCode() {
        return objectCode;
    }

    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getPkID() {
        return pkID;
    }

    public void setPkID(Long pkID) {
        this.pkID = pkID;
    }


    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }
}
