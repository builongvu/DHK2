package vn.vissoft.dashboard.model;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.*;

/**
 * Created by DESKTOP-6ORLP4T on 18/10/2019 17:51
 */

@Entity
@Table(name="vtt_position")
public class VttPosition {

    private String positionCode;
    private Timestamp createDate;
    private String user;
    private Long id;
    private String groupChannelCode;
    private Double status;

    public VttPosition() {
    }

    public VttPosition(String positionCode, Timestamp createDate, String user,  String groupChannelCode, Double status) {
        this.positionCode = positionCode;
        this.createDate = createDate;
        this.user = user;
        this.groupChannelCode = groupChannelCode;
        this.status = status;
    }

    @Column(name="position_code")
    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }
    
    @Column(name="created_date")
    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
    
    
    @Column(name="created_user")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    
    @Column(name="group_channel_code")
    public String getGroupChannelCode() {
        return groupChannelCode;
    }

    public void setGroupChannelCode(String groupChannelCode) {
        this.groupChannelCode = groupChannelCode;
    }
    
    
    @Column(name="status")
    public Double getStatus() {
        return status;
    }

    public void setStatus(Double status) {
        this.status = status;
    }

}
