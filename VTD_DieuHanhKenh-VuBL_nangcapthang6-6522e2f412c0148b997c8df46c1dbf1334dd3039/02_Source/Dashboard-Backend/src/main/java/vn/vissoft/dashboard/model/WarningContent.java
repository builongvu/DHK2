package vn.vissoft.dashboard.model;

import javax.persistence.*;

@Entity
@Table(name = "WARNING_CONTENT")
public class WarningContent {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long mlngId;

    @Column(name = "CONTENT")
    private  String mstrContent;

    @Column(name = "STATUS")
    private  String mstrStatus;

    public WarningContent() {
    }

    public Long getMlngId() {
        return mlngId;
    }

    public String getMstrContent() {
        return mstrContent;
    }

    public String getMstrStatus() {
        return mstrStatus;
    }

    public void setMlngId(Long mlngId) {
        this.mlngId = mlngId;
    }

    public void setMstrContent(String mstrContent) {
        this.mstrContent = mstrContent;
    }

    public void setMstrStatus(String mstrStatus) {
        this.mstrStatus = mstrStatus;
    }
}
