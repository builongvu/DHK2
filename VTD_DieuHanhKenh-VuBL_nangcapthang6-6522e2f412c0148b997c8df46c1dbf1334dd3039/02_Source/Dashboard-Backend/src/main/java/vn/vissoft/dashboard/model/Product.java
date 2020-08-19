package vn.vissoft.dashboard.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PRODUCT")
public class Product {

    private Long mlngId;
    private String mstrCode;
    private String mstrName;
    private String mstrStatus;



    @Column(name = "Id")
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getMlngId() {
        return mlngId;
    }

    public void setMlngId(Long mlngId) {
        this.mlngId = mlngId;
    }

    @Basic
    @Column(name = "CODE")
    public String getMstrCode() {
        return mstrCode;
    }

    public void setMstrCode(String mstrCode) {
        this.mstrCode = mstrCode;
    }
    @Basic
    @Column(name = "NAME")
    public String getMstrName() {
        return mstrName;
    }

    public void setMstrName(String mstrName) {
        this.mstrName = mstrName;
    }
    @Basic
    @Column(name = "STATUS")
    public String getMstrStatus() {
        return mstrStatus;
    }

    public void setMstrStatus(String mstrStatus) {
        this.mstrStatus = mstrStatus;
    }









}
