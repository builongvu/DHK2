package vn.vissoft.dashboard.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "mapping_group_channel")
public class MappingGroupChannel {
    private Long id;
    private String groupChannelCode;
    private String groupChannelName;
    private String vdsChannelCode;
    private String vdsChannelName;
    private String status;
    private String user;
    private Timestamp createDate;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapping_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Column(name = "vds_channel_code")
    public String getVdsChannelCode() {
        return vdsChannelCode;
    }

    public void setVdsChannelCode(String vdsChannelCode) {
        this.vdsChannelCode = vdsChannelCode;
    }

    @Column(name = "vds_channel_name")
    public String getVdsChannelName() {
        return vdsChannelName;
    }

    public void setVdsChannelName(String vdsChannelName) {
        this.vdsChannelName = vdsChannelName;
    }

    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "create_date")
    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    @Column(name = "user")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
