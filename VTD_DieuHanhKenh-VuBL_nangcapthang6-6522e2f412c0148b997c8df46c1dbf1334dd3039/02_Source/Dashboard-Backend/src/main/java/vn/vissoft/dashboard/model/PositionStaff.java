package vn.vissoft.dashboard.model;

import javax.persistence.*;

@Entity
@Table(name = "position_staff")
public class PositionStaff {

    private long id;
    private String ttnsCode;
    private String positionCode;
    private String positionName;
    private String status;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "ttns_code")
    public String getTtnsCode() {
        return ttnsCode;
    }

    public void setTtnsCode(String ttnsCode) {
        this.ttnsCode = ttnsCode;
    }

    @Column(name = "position_code")
    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    @Column(name = "position_name")
    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
