package vn.vissoft.dashboard.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "CYCLE")
public class Cycle {

    private Long id;
    private String code;
    private String name;
    private String status;
    private int assign;
    private int dashboard;

    @Id
    @NotNull
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "CODE")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "STATUS")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Basic
    @Column(name = "ASSIGN")
    public int getAssign() {
        return assign;
    }

    public void setAssign(int assign) {
        this.assign = assign;
    }

    @Basic
    @Column(name = "DASHBOARD")
    public int getDashboard() {
        return dashboard;
    }

    public void setDashboard(int dashboard) {
        this.dashboard = dashboard;
    }
}
