package vn.vissoft.dashboard.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "vds_score_ranking_daily")
public class VdsScoreRankingDaily {

    private Long id;
    private Long prdId;
    private String shopCode;
    private String nhanVien;
    private String vdsChannelCode;
    private double score;
    private double scoreMax;
    private int rank;
    private double scoreN1;
    private double scoreMaxN1;
    private int rankN1;
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

    @Column(name = "prd_id")
    public Long getPrdId() {
        return prdId;
    }

    public void setPrdId(Long prdId) {
        this.prdId = prdId;
    }

    @Column(name = "don_vi")
    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    @Column(name = "nhan_vien")
    public String getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(String nhanVien) {
        this.nhanVien = nhanVien;
    }

    @Column(name = "kenh")
    public String getVdsChannelCode() {
        return vdsChannelCode;
    }

    public void setVdsChannelCode(String vdsChannelCode) {
        this.vdsChannelCode = vdsChannelCode;
    }

    @Column(name = "score")
    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Column(name = "score_max")
    public double getScoreMax() {
        return scoreMax;
    }

    public void setScoreMax(double scoreMax) {
        this.scoreMax = scoreMax;
    }

    @Column(name = "rank")
    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Column(name = "score_n1")
    public double getScoreN1() {
        return scoreN1;
    }

    public void setScoreN1(double scoreN1) {
        this.scoreN1 = scoreN1;
    }

    @Column(name = "score_max_n1")
    public double getScoreMaxN1() {
        return scoreMaxN1;
    }

    public void setScoreMaxN1(double scoreMaxN1) {
        this.scoreMaxN1 = scoreMaxN1;
    }

    @Column(name = "rank_n1")
    public int getRankN1() {
        return rankN1;
    }

    public void setRankN1(int rankN1) {
        this.rankN1 = rankN1;
    }

    @Column(name = "create_date")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
