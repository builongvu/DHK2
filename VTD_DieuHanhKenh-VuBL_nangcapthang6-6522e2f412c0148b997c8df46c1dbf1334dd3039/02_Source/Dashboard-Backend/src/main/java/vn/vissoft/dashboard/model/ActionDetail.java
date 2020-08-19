package vn.vissoft.dashboard.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "ACTION_DETAIL")
public class ActionDetail implements Serializable {
    private Long Id;
    private Long actionAuditId;
    private String columnName;
    private String oldValue;
    private String newValue;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic
    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        this.Id = id;
    }
    @Basic
    @Column(name = "ACTION_AUDIT_ID")
    public Long getActionAuditId() {
        return actionAuditId;
    }

    public void setActionAuditId(Long actionAuditId) {
        this.actionAuditId = actionAuditId;
    }
    @Basic
    @Column(name = "COLUMN_NAME")
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    @Basic
    @Column(name = "OLD_VALUE")
    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }
    @Basic
    @Column(name = "NEW_VALUE")
    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}
