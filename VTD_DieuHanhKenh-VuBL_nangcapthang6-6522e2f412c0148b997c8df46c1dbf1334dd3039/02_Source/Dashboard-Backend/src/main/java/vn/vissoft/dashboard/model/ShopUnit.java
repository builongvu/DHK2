package vn.vissoft.dashboard.model;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name = "shop_unit")
public class ShopUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long mlngId;
    @Column(name = "service_id")
    private Long mlngServiceId;
    @Column(name = "shop_code")
    private String mstrShopCode;
    @Column(name = "vds_channel_code")
    private String mstrVdsChannelCode;
    @Column(name = "unit_code")
    private String mstrUnitCode;
    @Column(name = "from_date")
    private Date mdtFromDate;
    @Column(name = "to_date")
    private Date mdtToDate;
    @Column(name = "status")
    private String mstrStatus;

    public ShopUnit() {

    }

    public Long getMlngId() {
        return mlngId;
    }

    public void setMlngId(Long mlngId) {
        this.mlngId = mlngId;
    }

    public Long getMlngServiceId() {
        return mlngServiceId;
    }

    public void setMlngServiceId(Long mlngServiceId) {
        this.mlngServiceId = mlngServiceId;
    }

    public String getMstrShopCode() {
        return mstrShopCode;
    }

    public void setMstrShopCode(String mstrShopCode) {
        this.mstrShopCode = mstrShopCode;
    }

    public String getMstrVdsChannelCode() {
        return mstrVdsChannelCode;
    }

    public void setMstrVdsChannelCode(String mstrVdsChanelCode) {
        this.mstrVdsChannelCode = mstrVdsChanelCode;
    }

    public String getMstrUnitCode() {
        return mstrUnitCode;
    }

    public void setMstrUnitCode(String mstrUnitCode) {
        this.mstrUnitCode = mstrUnitCode;
    }

    public Date getMdtFromDate() {
        return mdtFromDate;
    }

    public void setMdtFromDate(Date mdtFromDate) {
        this.mdtFromDate = mdtFromDate;
    }

    public Date getMdtToDate() {
        return mdtToDate;
    }

    public void setMdtToDate(Date mdtToDate) {
        this.mdtToDate = mdtToDate;
    }

    public String getMstrStatus() {
        return mstrStatus;
    }

    public void setMstrStatus(String mstrStatus) {
        this.mstrStatus = mstrStatus;
    }
}
