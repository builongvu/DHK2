package vn.vissoft.dashboard.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;

@Entity
@Table(name="service_score")
public class ServiceScore {

    private Long id;
    private Long serviceId;
    private String vdsChannelCode;
    private String shopCode;
    private String staffCode;
    private String status;
    private Double score;
    private Date fromDate;
    private Date toDate;
    private Double scoreMax;

    public ServiceScore() {
    }

    public ServiceScore(Long serviceId, String vdsChannelCode, String shopCode, String staffCode, String status, Double score, Date fromDate,Date toDate, Double scoreMax) {
        this.serviceId = serviceId;
        this.vdsChannelCode = vdsChannelCode;
        this.shopCode = shopCode;
        this.staffCode = staffCode;
        this.status = status;
        this.score = score;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.scoreMax = scoreMax;
    }

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "service_id")
    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
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

    @Column(name = "staff_code")
    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "score")
    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
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

    @Column(name = "score_max")
    public Double getScoreMax() {
        return scoreMax;
    }

    public void setScoreMax(Double scoreMax) {
        this.scoreMax = scoreMax;
    }
}
