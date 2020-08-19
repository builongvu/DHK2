package vn.vissoft.dashboard.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "service_warning_config")
public class TargetManagement {
    private Long mlgId;
    private Long mlgServiceId;
    private String mstrvdscode;
    private Long mlgwarlevel;
    private String status;
    private Double from_value;
    private Double to_value;
    private Long exp;

    @Column(name = "ID")
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)

    public Long getMlgId() {
        return mlgId;
    }

    public void setMlgId(Long mlgId) {
        this.mlgId = mlgId;
    }

    @Basic
    @Column(name = "SERVICE_ID")
    public Long getMlgServiceId() {
        return mlgServiceId;
    }

    public void setMlgServiceId(Long mlgServiceId) {
        this.mlgServiceId = mlgServiceId;
    }

    @Basic
    @Column(name = "VDS_CHANNEL_CODE")
    public String getMstrvdscode() {
        return mstrvdscode;
    }

    public void setMstrvdscode(String mstrvdscode) {
        this.mstrvdscode = mstrvdscode;
    }

    @Column(name = "WANING_LEVEL")
    public Long getMlgwarlevel() {
        return mlgwarlevel;
    }

    public void setMlgwarlevel(Long mlgwarlevel) {
        this.mlgwarlevel = mlgwarlevel;
    }

    @Column(name = "STATUS")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "FROM_VALUE")
    public Double getFrom_value() {
        return from_value;
    }

    public void setFrom_value(Double from_value) {
        this.from_value = from_value;
    }

    @Column(name = "TO_VALUE")
    public Double getTo_value() {
        return to_value;
    }

    public void setTo_value(Double to_value) {
        this.to_value = to_value;
    }

    @Column(name = "EXP")
    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }
}
