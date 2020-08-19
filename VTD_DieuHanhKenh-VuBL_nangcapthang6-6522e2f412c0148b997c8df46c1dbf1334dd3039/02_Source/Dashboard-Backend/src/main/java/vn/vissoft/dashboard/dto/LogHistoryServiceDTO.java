package vn.vissoft.dashboard.dto;

import java.sql.Date;

public class LogHistoryServiceDTO extends BaseDTO {

    private String actionCode;
    private String actionDateTime;
    private String pkID;
    private String user;
    private String columnName;
    private String oldValue;
    private String newValue;
    private String objectName;
    private Long no;


    public String getPkID() {
        return pkID;
    }

    public void setPkID(String pkID) {
        this.pkID = pkID;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getActionDateTime() {
        return actionDateTime;
    }

    public void setActionDateTime(String actionDateTime) {
        this.actionDateTime = actionDateTime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public Long getNo() {
        return no;
    }

    public void setNo(Long no) {
        this.no = no;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
}
