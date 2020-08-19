package vn.vissoft.dashboard.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "AP_PARAM")
public class ApParam {

    private Long mlngId;
    private String mstrType;
    private String mstrCode;
    private String mstrName;
    private String mstrDescription;
    private String mstrStatus;
    private Date mdtCreateDatetime;
    private String value;

    @Basic
    @Column(name = "TYPE")
    public String getType() {
        return mstrType;
    }

    public void setType(String pstrType) {
        this.mstrType = pstrType;
    }

    @Basic
    @Column(name = "CODE")
    public String getCode() {
        return mstrCode;
    }

    public void setCode(String pstrCode) {
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

    @Basic
    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return mstrDescription;
    }

    public void setDescription(String pstrDescription) {
        this.mstrDescription = pstrDescription;
    }

    @Basic
    @Column(name = "STATUS")
    public String getStatus() {
        return mstrStatus;
    }

    public void setStatus(String pstrStatus) {
        this.mstrStatus = pstrStatus;
    }

    @Basic
    @Column(name = "CREATE_DATE")
    public Date getCreateDatetime() {
        return mdtCreateDatetime;
    }

    public void setCreateDatetime(Date pdtCreateDatetime) {
        this.mdtCreateDatetime = pdtCreateDatetime;
    }

    @Column(name = "ID")
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return mlngId;
    }

    public void setId(Long plngId) {
        this.mlngId = plngId;
    }

    @Column(name = "VALUE")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
