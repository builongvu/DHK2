package vn.vissoft.dashboard.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Table(name = "QUEUE_SMS_EMAIL")
public class QueueSmsEmail {

    private Long id;
    private String source;
    private String type;
    private String isdnEmail;
    private String content;
    private Timestamp importDatetime;
    private Timestamp executeDatetime;
    private String execute_status;

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "SOURCE")
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Column(name = "TYPE")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "ISDN_EMAIL")
    public String getIsdnEmail() {
        return isdnEmail;
    }

    public void setIsdnEmail(String isdnEmail) {
        this.isdnEmail = isdnEmail;
    }

    @Column(name = "CONTENT")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(name = "IMPORT_DATETIME")
    public Timestamp getImportDatetime() {
        return importDatetime;
    }

    public void setImportDatetime(Timestamp importDatetime) {
        this.importDatetime = importDatetime;
    }

    @Column(name = "EXECUTE_DATETIME")
    public Timestamp getExecuteDatetime() {
        return executeDatetime;
    }

    public void setExecuteDatetime(Timestamp executeDatetime) {
        this.executeDatetime = executeDatetime;
    }

    @Column(name = "EXECUTE_STATUS")
    public String getExecute_status() {
        return execute_status;
    }

    public void setExecute_status(String execute_status) {
        this.execute_status = execute_status;
    }
}
