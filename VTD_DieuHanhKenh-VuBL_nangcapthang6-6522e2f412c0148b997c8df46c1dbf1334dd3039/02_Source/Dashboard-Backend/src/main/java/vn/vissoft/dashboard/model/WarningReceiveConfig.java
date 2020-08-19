package vn.vissoft.dashboard.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "WARNING_RECEIVE_CONFIG")
public class WarningReceiveConfig {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long mlngId;
    @Column(name = "SHOP_CODE")
    private String mstrShopCode;
    @Column(name = "VDS_CHANNEL_CODE")
    private String mstrChannel;
    @Column(name = "WARNING_LEVEL")
    private Integer mintWarningLevel;
    @Column(name = "INFORM_LEVEL")
    private Integer mintInformLevel;
    @Column(name = "STATUS")
    private String mstrStatus;
    @Column(name = "USER")
    private String mstrUser;
    @Column(name = "CREATE_DATE")
    private Date mdtCreateDate;

    public WarningReceiveConfig() {
    }

    public String getMstrChannel() {
        return mstrChannel;
    }

    public void setMstrChannel(String mstrChannel) {
        this.mstrChannel = mstrChannel;
    }

    public Long getMlngId() {
        return mlngId;
    }

    public String getMstrShopCode() {
        return mstrShopCode;
    }

    public Integer getMintWarningLevel() {
        return mintWarningLevel;
    }

    public Integer getMintInformLevel() {
        return mintInformLevel;
    }

    public String getMstrStatus() {
        return mstrStatus;
    }

    public String getMstrUser() {
        return mstrUser;
    }

    public Date getMdtCreateDate() {
        return mdtCreateDate;
    }

    public void setMlngId(Long mlngId) {
        this.mlngId = mlngId;
    }

    public void setMstrShopCode(String mstrShopCode) {
        this.mstrShopCode = mstrShopCode;
    }

    public void setMintWarningLevel(Integer mintWarningLevel) {
        this.mintWarningLevel = mintWarningLevel;
    }

    public void setMintInformLevel(Integer mintInformLevel) {
        this.mintInformLevel = mintInformLevel;
    }

    public void setMstrStatus(String mstrStatus) {
        this.mstrStatus = mstrStatus;
    }

    public void setMstrUser(String mstrUser) {
        this.mstrUser = mstrUser;
    }

    public void setMdtCreateDate(Date mdtCreateDate) {
        this.mdtCreateDate = mdtCreateDate;
    }
}
