package vn.vissoft.dashboard.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "vds_group_channel")
public class VdsGroupChannel {

    private Long mlngId;
    private String mstrCode;
    private String mstrName;
    private String mstrStatus;
    private String user;
    private Date createdDate;
    @Column(name = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return mlngId;
    }

    public void setId(Long plngId) {
        this.mlngId = plngId;
    }

    @Column(name = "vds_channel_code", unique = true)
    public String getCode() {
        return mstrCode;
    }

    public void setCode(String pstrCode) {
        this.mstrCode = pstrCode;
    }

    @Column(name = "vds_channel_name")
    public String getName() {
        return mstrName;
    }

    public void setName(String pstrName) {
        this.mstrName = pstrName;
    }
    
    @Column(name = "STATUS")
    public String getStatus() {
        return mstrStatus;
    }

    public void setStatus(String pstrStatus) {
        this.mstrStatus = pstrStatus;
    }

    @Column(name = "created_date")
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Column(name = "user")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
