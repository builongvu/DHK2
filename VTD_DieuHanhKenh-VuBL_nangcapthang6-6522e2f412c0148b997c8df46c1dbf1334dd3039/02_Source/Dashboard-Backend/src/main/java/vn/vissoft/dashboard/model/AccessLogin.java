package vn.vissoft.dashboard.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "ACCESS_LOGIN")
public class AccessLogin {

    private Long id;
    private String username;
    private String ip;
    private Timestamp timeLogin;
    private String browser;

    public AccessLogin(String username, String ip, Timestamp time, String browser) {
        this.username = username;
        this.ip = ip;
        this.timeLogin = time;
        this.browser = browser;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "ID")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "USER_NAME")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "IP")
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Column(name = "TIME_LOGIN")
    public Timestamp getTimeLogin() {
        return timeLogin;
    }

    public void setTimeLogin(Timestamp time) {
        this.timeLogin = time;
    }

    @Column(name = "BROWSER")
    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }
}
