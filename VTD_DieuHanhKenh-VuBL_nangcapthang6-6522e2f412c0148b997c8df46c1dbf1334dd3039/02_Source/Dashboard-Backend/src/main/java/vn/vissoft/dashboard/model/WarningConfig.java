package vn.vissoft.dashboard.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "service_warning_config")
public class WarningConfig {
    private Long wcID;
    private Long svID;
    private String vdscCode;
    private Integer wlevel;
    private String wStatus;
    private Double wfvalue;
    private Double wovalue;
    private String wexp;

    public WarningConfig() {
    }

    public WarningConfig(Long svID, String vdscCode, Integer wlevel, String wStatus, Double wfvalue, Double wovalue, String wexp) {
        this.svID = svID;
        this.vdscCode = vdscCode;
        this.wlevel = wlevel;
        this.wStatus = wStatus;
        this.wfvalue = wfvalue;
        this.wovalue = wovalue;
        this.wexp = wexp;
    }

    @Column(name = "id")
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getWcID() {
        return wcID;
    }


    public void setWcID(Long wcID) {
        this.wcID = wcID;
    }

    @Basic
    @Column(name = "service_id")
    public Long getSvID() {
        return svID;
    }

    public void setSvID(Long svID) {
        this.svID = svID;
    }

    @Basic
    @Column(name = "VDS_CHANNEL_CODE")
    public String getVdscCode() {
        return vdscCode;
    }

    public void setVdscCode(String vdscCode) {
        this.vdscCode = vdscCode;
    }

    @Basic
    @Column(name = "warning_level")
    public Integer getWlevel() {
        return wlevel;
    }

    public void setWlevel(Integer wlevel) {
        this.wlevel = wlevel;
    }

    @Basic
    @Column(name = "status")
    public String getwStatus() {
        return wStatus;
    }

    public void setwStatus(String wStatus) {
        this.wStatus = wStatus;
    }

    @Basic
    @Column(name = "from_value")
    public Double getWfvalue() {
        return wfvalue;
    }

    public void setWfvalue(Double wfvalue) {
        this.wfvalue = wfvalue;
    }

    @Basic
    @Column(name = "to_value")
    public Double getWovalue() {
        return wovalue;
    }

    public void setWovalue(Double wovalue) {
        this.wovalue = wovalue;
    }

    @Basic
    @Column(name = "exp")
    public String getWexp() {
        return wexp;
    }

    public void setWexp(String wexp) {
        this.wexp = wexp;
    }

}
