package vn.vissoft.dashboard.model;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.*;

/**
 * Created by DESKTOP-6ORLP4T on 18/10/2019 17:52
 */

@Entity
@Table(name="vtt_group_channel_sale")
public class VttGroupChannelSale {

    private Long id;
    private String groupChannelCode;
    private Long channelTypeId;
    private String status;
    private String createUser;
    private Timestamp createDate;

    public VttGroupChannelSale() {
    }

    public VttGroupChannelSale(String status, Long channelTypeId, String createUser, Timestamp createDate, String groupChannelCode) {
        this.status = status;
        this.channelTypeId = channelTypeId;
        this.createUser = createUser;
        this.createDate = createDate;
        this.groupChannelCode = groupChannelCode;
    }

    @Column(name="STATUS")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name="channel_type_id")
    public Long getChannelTypeId() {
        return channelTypeId;
    }

    public void setChannelTypeId(Long channelTypeId) {
        this.channelTypeId = channelTypeId;
    }
    
    @Column(name="created_user")
    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
    
    
    @Column(name="created_date")
    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
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

}
