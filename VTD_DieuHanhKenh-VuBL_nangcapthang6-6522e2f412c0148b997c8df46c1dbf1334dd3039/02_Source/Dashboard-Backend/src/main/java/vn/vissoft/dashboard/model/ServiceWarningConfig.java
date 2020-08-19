package vn.vissoft.dashboard.model;

import java.sql.Blob;
import java.util.Date;
import javax.persistence.*;

/**
 * Created by PC on 07/11/2019 11:47
 */

@Entity
@Table(name="service_warning_config")
public class ServiceWarningConfig {

    private Double fromValue;
    private Long serviceId;
    private String vdsChannelCode;
    private Double toValue;
    private String exp;
    private Long id;
    private Integer waningLevel;
    private String status;

    
    
    @Column(name="from_value")
    public Double getFromValue() {
        return fromValue;
    }

    public void setFromValue(Double fromValue) {
        this.fromValue = fromValue;
    }
    
    
    @Column(name="service_id")
    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }
    
    
    @Column(name="VDS_CHANNEL_CODE")
    public String getVdsChannelCode() {
        return vdsChannelCode;
    }

    public void setVdsChannelCode(String vdsChannelCode) {
        this.vdsChannelCode = vdsChannelCode;
    }
    
    
    @Column(name="to_value")
    public Double getToValue() {
        return toValue;
    }

    public void setToValue(Double toValue) {
        this.toValue = toValue;
    }
    
    
    @Column(name="exp")
    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    
    @Column(name="warning_level")
    public Integer getWaningLevel() {
        return waningLevel;
    }

    public void setWaningLevel(Integer waningLevel) {
        this.waningLevel = waningLevel;
    }
    
    
    @Column(name="status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
