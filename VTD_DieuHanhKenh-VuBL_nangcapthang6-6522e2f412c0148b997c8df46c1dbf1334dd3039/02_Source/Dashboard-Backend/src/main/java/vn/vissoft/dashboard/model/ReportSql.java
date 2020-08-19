package vn.vissoft.dashboard.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "REPORT_SQL")
public class ReportSql {

    private Long mlngId;
    private String mstrCode;
    private String mstrName;
    private String mstrContent;
    private String mstrStatus;


    @Column(name = "Id")
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return mlngId;
    }

    public void setId(Long plngId) {
        this.mlngId = plngId;
    }

    @Basic
    @Column(name = "SQL_CODE")
    public String getCode() {
        return mstrCode;
    }

    public void setCode(String pstrCode) {
        this.mstrCode = pstrCode;
    }

    @Basic
    @Column(name = "SQL_NAME")
    public String getName() {
        return mstrName;
    }

    public void setName(String pstrName) {
        this.mstrName = pstrName;
    }

    @Basic
    @Column(name = "SQL_CONTENT")
    public String getContent() {
        return mstrContent;
    }

    public void setContent(String pstrContent) {
        this.mstrContent = pstrContent;
    }

    @Basic
    @Column(name = "STATUS")
    public String getStatus() {
        return mstrStatus;
    }

    public void setStatus(String pstrStatus) {
        this.mstrStatus = pstrStatus;
    }

}
