package vn.vissoft.dashboard.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name="vtt_group_channel")
public class VttGroupChannel {
    private Long groupId;
    private String groupChannelCode;
    private String groupChannelName;
    private String status;
    private String user;
    private Timestamp createDate;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="group_id")
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @Column(name = "group_channel_code")
    public String getGroupChannelCode() {
        return groupChannelCode;
    }

    public void setGroupChannelCode(String groupChannelCode) {
        this.groupChannelCode = groupChannelCode;
    }

    @Column(name = "group_channel_name")
    public String getGroupChannelName() {
        return groupChannelName;
    }

    public void setGroupChannelName(String groupChannelName) {
        this.groupChannelName = groupChannelName;
    }

    @Column(name ="group_status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "user")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Column(name = "create_date")
    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
