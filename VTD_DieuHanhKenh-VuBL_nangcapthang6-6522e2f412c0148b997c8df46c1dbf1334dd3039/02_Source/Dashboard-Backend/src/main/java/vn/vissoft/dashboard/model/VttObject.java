package vn.vissoft.dashboard.model;

import javax.persistence.*;
import java.sql.Timestamp;
@Entity
@Table(name = "vtt_object")
public class VttObject {
    private Long id;
    private Long objectId;
    private String groupChannelCode;
    private String objectCode;
    private String objectName;
    private String status;
    private String user;
    private Timestamp createDate;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "object_id")
    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    @Column(name = "group_channel_code")
    public String getGroupChannelCode() {
        return groupChannelCode;
    }

    public void setGroupChannelCode(String groupChannelCode) {
        this.groupChannelCode = groupChannelCode;
    }

    @Column(name = "object_code")
    public String getObjectCode() {
        return objectCode;
    }

    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

    @Column(name = "object_name")
    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    @Column(name = "status")
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
