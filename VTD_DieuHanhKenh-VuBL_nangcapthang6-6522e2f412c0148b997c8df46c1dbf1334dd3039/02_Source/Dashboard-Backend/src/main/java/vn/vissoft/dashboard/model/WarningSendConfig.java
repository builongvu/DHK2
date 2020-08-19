package vn.vissoft.dashboard.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "WARNING_SEND_CONFIG")
public class WarningSendConfig {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long mlngId;

    @Column(name = "SERVICE_ID")
    private Long mlngServiceId;

    @Column(name = "warning_level")
    private Integer mintWarningLevel;

    @Column(name = "EMAIL")
    private Integer mintEmail;

    @Column(name = "SMS")
    private Integer mintSms;

    @Column(name = "INFORM_LEVEL")
    private Integer mintInformLevel;

    @Column(name = "ID_CONTENT")
    private Long mlngIdContent;

    @Column(name = "USER")
    private String mstrUser;

    @Column(name = "CREATE_DATE")
    private Date mdtCreateDate;

    @Column(name = "STATUS")
    private String mstrStatus;

    public WarningSendConfig() {
    }

    public void setMstrStatus(String mstrStatus) {
        this.mstrStatus = mstrStatus;
    }

    public String getMstrStatus() {
        return mstrStatus;
    }

    public Long getMlngId() {
        return mlngId;
    }

    public Long getMlngServiceId() {
        return mlngServiceId;
    }

    public Integer getMintWarningLevel() {
        return mintWarningLevel;
    }

    public Integer getMintEmail() {
        return mintEmail;
    }

    public Integer getMintSms() {
        return mintSms;
    }

    public Integer getMintInformLevel() {
        return mintInformLevel;
    }

    public Long getMlngIdContent() {
        return mlngIdContent;
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

    public void setMlngServiceId(Long mlngServiceId) {
        this.mlngServiceId = mlngServiceId;
    }

    public void setMintWarningLevel(Integer mintWarningLevel) {
        this.mintWarningLevel = mintWarningLevel;
    }

    public void setMintEmail(Integer mintEmail) {
        this.mintEmail = mintEmail;
    }

    public void setMintSms(Integer mintSms) {
        this.mintSms = mintSms;
    }

    public void setMintInformLevel(Integer mintInformLevel) {
        this.mintInformLevel = mintInformLevel;
    }

    public void setMlngIdContent(Long mlngIdContent) {
        this.mlngIdContent = mlngIdContent;
    }

    public void setMstrUser(String mstrUser) {
        this.mstrUser = mstrUser;
    }

    public void setMdtCreateDate(Date mdtCreateDate) {
        this.mdtCreateDate = mdtCreateDate;
    }
}
