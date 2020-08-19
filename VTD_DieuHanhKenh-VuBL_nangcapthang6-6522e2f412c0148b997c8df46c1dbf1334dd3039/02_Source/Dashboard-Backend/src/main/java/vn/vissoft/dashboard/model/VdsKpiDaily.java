package vn.vissoft.dashboard.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "vds_kpi_daily")
public class VdsKpiDaily {

    private Long id;
    private Long prdId;
    private String donVi;
    private String nhanVien;
    private String kenh;
    private int slDatCt;
    private int slKhongDatCt;
    private Date createDate;

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "PRD_ID")
    public Long getPrdId() {
        return prdId;
    }

    public void setPrdId(Long prdId) {
        this.prdId = prdId;
    }

    @Column(name = "DON_VI")
    public String getDonVi() {
        return donVi;
    }

    public void setDonVi(String donVi) {
        this.donVi = donVi;
    }

    @Column(name = "NHAN_VIEN")
    public String getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(String nhanVien) {
        this.nhanVien = nhanVien;
    }

    @Column(name = "KENH")
    public String getKenh() {
        return kenh;
    }

    public void setKenh(String kenh) {
        this.kenh = kenh;
    }

    @Column(name = "SL_DAT_CT")
    public int getSlDatCt() {
        return slDatCt;
    }

    public void setSlDatCt(int slDatCt) {
        this.slDatCt = slDatCt;
    }

    @Column(name = "SL_KHONG_DAT_CT")
    public int getSlKhongDatCt() {
        return slKhongDatCt;
    }

    public void setSlKhongDatCt(int slKhongDatCt) {
        this.slKhongDatCt = slKhongDatCt;
    }

    @Column(name = "CREATE_DATE")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
