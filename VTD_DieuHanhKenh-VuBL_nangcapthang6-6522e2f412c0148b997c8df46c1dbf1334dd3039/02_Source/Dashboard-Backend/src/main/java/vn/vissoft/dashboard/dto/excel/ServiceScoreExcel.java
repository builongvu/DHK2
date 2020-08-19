package vn.vissoft.dashboard.dto.excel;

import vn.vissoft.dashboard.helper.excelreader.annotation.*;

import java.util.Objects;

@ExcelEntity(dataStartRowIndex = 6, signalConstant = "IMPORT_SERVICE_SCORE")
public class ServiceScoreExcel extends BaseExcelEntity{
    private String vdsChannelCode;
    private String shopCode;
    private String staffCode;
    private String serviceCode;
    private Double score;
    private String fromDate;
    private String toDate;
    private Double scoreMax;
    private int index;

    @ExcelColumn(name = "B",nullable = false)
    public String getVdsChannelCode() {
        return vdsChannelCode;
    }

    public void setVdsChannelCode(String vdsChannelCode) {
        this.vdsChannelCode = vdsChannelCode;
    }

    @ExcelColumn(name = "C",nullable = false)
    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    @ExcelColumn(name = "D",nullable = true)
    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    @ExcelColumn(name = "E",nullable = false)
    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    @ExcelColumn(name = "F",nullable = false,regex = "^(0(\\.\\d{1,2})?|1(\\.0)?)$")
    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @ExcelColumn(name = "G",nullable = false,regex = "^([0-2][0-9]|(3)[0-1])(\\/)(((0)[0-9])|((1)[0-2]))(\\/)\\d{4}$")
//    @TypeGenerator(strategy = Strategy.DATE,format = "dd/MM/yyyy")
    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    @ExcelColumn(name = "H",nullable = true,regex = "^([0-2][0-9]|(3)[0-1])(\\/)(((0)[0-9])|((1)[0-2]))(\\/)\\d{4}$")
//    @TypeGenerator(strategy = Strategy.DATE,format = "dd/MM/yyyy")
    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    @ExcelColumn(name = "I",nullable = false,regex = "^(0(\\.\\d{1,2})?|1(\\.0)?)$")
    public Double getScoreMax() {
        return scoreMax;
    }

    public void setScoreMax(Double scoreMax) {
        this.scoreMax = scoreMax;
    }

    @IndexColumn
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceScoreExcel that = (ServiceScoreExcel) o;
        return Objects.equals(vdsChannelCode, that.vdsChannelCode) &&
                Objects.equals(shopCode, that.shopCode) &&
                Objects.equals(staffCode, that.staffCode) &&
                Objects.equals(serviceCode, that.serviceCode) &&
                Objects.equals(score, that.score) &&
                Objects.equals(fromDate, that.fromDate) &&
                Objects.equals(toDate, that.toDate) &&
                Objects.equals(scoreMax, that.scoreMax);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vdsChannelCode, shopCode, staffCode, serviceCode, score, fromDate, toDate, scoreMax);
    }
}
