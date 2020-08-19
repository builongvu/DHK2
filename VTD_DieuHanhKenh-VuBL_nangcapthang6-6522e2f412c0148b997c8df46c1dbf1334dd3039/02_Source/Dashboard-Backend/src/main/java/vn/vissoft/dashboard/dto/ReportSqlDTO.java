package vn.vissoft.dashboard.dto;

import java.sql.Date;

public class ReportSqlDTO {

    private Long mlngId;
    private String mstrCode;
    private String mstrName;
    private String mstrContent;
    private String mstrStatus;

    public Long getId() {
        return mlngId;
    }

    public void setId(Long plngId) {
        this.mlngId = plngId;
    }

    public String getCode() {
        return mstrCode;
    }

    public void setCode(String pstrCode) {
        this.mstrCode = pstrCode;
    }

    public String getName() {
        return mstrName;
    }

    public void setName(String pstrName) {
        this.mstrName = pstrName;
    }

    public String getContent() {
        return mstrContent;
    }

    public void setContent(String pstrContent) {
        this.mstrContent = pstrContent;
    }
    

    public String getStatus() {
        return mstrStatus;
    }

    public void setStatus(String pstrStatus) {
        this.mstrStatus = pstrStatus;
    }
}
