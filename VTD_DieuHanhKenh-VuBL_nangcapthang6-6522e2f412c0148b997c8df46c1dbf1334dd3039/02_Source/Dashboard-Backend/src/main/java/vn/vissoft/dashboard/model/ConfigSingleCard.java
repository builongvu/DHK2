package vn.vissoft.dashboard.model;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "CONFIG_SINGLE_CARD")
public class ConfigSingleCard {

    private Long mlngCardId;
    private String mstrCardName;
    private String mstrCardNameI18n;
    private String mstrCardSize;
    private Integer mintZoom;
    private Integer mintDrilldown;
    private Integer drillDownType;
    private Integer drillDownObjectId;
    private Long mlngServiceId;
    private Date mdtCreateDate;
    private Integer mintStatus;
    private Integer mintGroupId;
    private String mstrCardType;
    private int cardOrder;

    @Column(name = "CARD_ID")
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getCardId() {
        return mlngCardId;
    }

    public void setCardId(Long plngCardId) {
        this.mlngCardId = plngCardId;
    }

    @Basic
    @Column(name = "CARD_NAME")
    public String getCardName() {
        return mstrCardName;
    }

    public void setCardName(String pstrCardName) {
        this.mstrCardName = pstrCardName;
    }

    @Basic
    @Column(name = "CARD_NAME_I18N")
    public String getCardNameI18n() {
        return mstrCardNameI18n;
    }

    public void setCardNameI18n(String pstrCardNameI18n) {
        this.mstrCardNameI18n = pstrCardNameI18n;
    }

    @Basic
    @Column(name = "CARD_SIZE")
    public String getCardSize() {
        return mstrCardSize;
    }

    public void setCardSize(String pstrCardSize) {
        this.mstrCardSize = pstrCardSize;
    }

    @Basic
    @Column(name = "ZOOM")
    public Integer getZoom() {
        return mintZoom;
    }

    public void setZoom(Integer pintZoom) {
        this.mintZoom = pintZoom;
    }

    @Basic
    @Column(name = "DRILLDOWN")
    public Integer getDrilldown() {
        return mintDrilldown;
    }

    public void setDrilldown(Integer pintDrillDown) {
        this.mintDrilldown = pintDrillDown;
    }

    @Basic
    @Column(name = "DRILLDOWN_TYPE")
    public Integer getDrillDownType() {
        return drillDownType;
    }

    public void setDrillDownType(Integer drillDownType) {
        this.drillDownType = drillDownType;
    }

    @Basic
    @Column(name = "DRILLDOWN_OBJECT_ID")
    public Integer getDrillDownObjectId() {
        return drillDownObjectId;
    }

    public void setDrillDownObjectId(Integer drillDownObjectId) {
        this.drillDownObjectId = drillDownObjectId;
    }

    @Basic
    @Column(name = "SERVICE_ID")
    public Long getServiceId() {
        return mlngServiceId;
    }

    public void setServiceId(Long plngServiceId) {
        this.mlngServiceId = plngServiceId;
    }

    @Basic
    @Column(name = "CREATE_DATE")
    public Date getCreateDate() {
        return mdtCreateDate;
    }

    public void setCreateDate(Date pdtCreateDate) {
        this.mdtCreateDate = pdtCreateDate;
    }

    @Basic
    @Column(name = "STATUS")
    public Integer getStatus() {
        return mintStatus;
    }

    public void setStatus(Integer mintStatus) {
        this.mintStatus = mintStatus;
    }


    @Basic
    @Column(name = "GROUP_ID")
    public Integer getGroupId() {
        return mintGroupId;
    }

    public void setGroupId(Integer mintGroupId) {
        this.mintGroupId = mintGroupId;
    }

    @Basic
    @Column(name = "CARD_TYPE")
    public String getCardType() {
        return mstrCardType;
    }

    public void setCardType(String mstrCardType) {
        this.mstrCardType = mstrCardType;
    }

    @Basic
    @Column(name = "CARD_ORDER")
    public int getCardOrder() {
        return cardOrder;
    }

    public void setCardOrder(int cardOrder) {
        this.cardOrder = cardOrder;
    }
}
