package vn.vissoft.dashboard.dto.excel;

import vn.vissoft.dashboard.helper.excelreader.annotation.*;

import java.util.Objects;

@ExcelEntity(dataStartRowIndex = 5, signalConstant = "IMPORT_SERVICE")
public class ServiceExcel extends BaseExcelEntity {

    private String mstrServiceCode;
    private String mstrServiceName;
    private String mstrParentCode;
    private String mstrGroupCode;
    private String mstrServiceType;
    private String mstrServiceCalc;
    private String mstrServiceCycle;
    private String mstrServiceChannel;
    private String mstrFromDate;
    private String mstrToDate;
    private String mstrUnitCode;
    private String exp;
    private int mintIndex;

    @ExcelColumn(name = "B", nullable = false, regex = "^[_A-Za-z0-9]{1,51}")
    public String getMstrServiceCode() {
        return mstrServiceCode;
    }

    public void setMstrServiceCode(String mstrServiceCode) {
        this.mstrServiceCode = mstrServiceCode;
    }

    @ExcelColumn(name = "C", nullable = false, regex = "")
    public String getMstrServiceName() {
        return mstrServiceName;
    }

    public void setMstrServiceName(String mstrServiceName) {
        this.mstrServiceName = mstrServiceName;
    }

    @ExcelColumn(name = "D", nullable = true, regex = "^[_A-Za-z0-9]{1,51}")
    public String getMstrParentCode() {
        return mstrParentCode;
    }

    public void setMstrParentCode(String mstrParentCode) {
        this.mstrParentCode = mstrParentCode;
    }

    @ExcelColumn(name = "E", nullable = false,regex = "^[_A-Za-z0-9]{1,51}")
    public String getMstrGroupCode() {
        return mstrGroupCode;
    }

    public void setMstrGroupCode(String mstrGroupCode) {
        this.mstrGroupCode = mstrGroupCode;
    }

    @ExcelColumn(name = "F", nullable = false, regex = "")
    public String getMstrServiceChannel() {
        return mstrServiceChannel;
    }

    public void setMstrServiceChannel(String mstrServiceChannel) {
        this.mstrServiceChannel = mstrServiceChannel;
    }

    @ExcelColumn(name = "H", nullable = false)
    public String getMintServiceCycle() {
        return mstrServiceCycle;
    }

    public void setMintServiceCycle(String mstrServiceCycle) {
        this.mstrServiceCycle = mstrServiceCycle;
    }

    @ExcelColumn(name = "G", nullable = false)
    public String getMlongServiceType() {
        return mstrServiceType;
    }

    public void setMlongServiceType(String mstrServiceType) {
        this.mstrServiceType = mstrServiceType;
    }

    @ExcelColumn(name = "I",nullable = false,regex = "^([0-2][0-9]|(3)[0-1])(\\/)(((0)[0-9])|((1)[0-2]))(\\/)\\d{4}$")
//    @TypeGenerator(strategy = Strategy.DATE,format = "dd/MM/yyyy")
    public String getMstrFromDate() {
        return mstrFromDate;
    }

    public void setMstrFromDate(String mstrFromDate) {
        this.mstrFromDate = mstrFromDate;
    }

    @ExcelColumn(name = "J",nullable = true,regex = "^([0-2][0-9]|(3)[0-1])(\\/)(((0)[0-9])|((1)[0-2]))(\\/)\\d{4}$")
//    @TypeGenerator(strategy = Strategy.DATE,format = "dd/MM/yyyy")
    public String getMstrToDate() {
        return mstrToDate;
    }

    public void setMstrToDate(String mstrToDate) {
        this.mstrToDate = mstrToDate;
    }


    @ExcelColumn(name = "K", nullable = false, regex = "^[A-Za-z0-9]+")
    public String getMstrUnitCode() {
        return mstrUnitCode;
    }

    public void setMstrUnitCode(String mstrUnitCode) {
        this.mstrUnitCode = mstrUnitCode;
    }

    @ExcelColumn(name = "L", nullable = true, regex = "")
    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    @ExcelColumn(name = "M", nullable = false, regex = "")
    public String getMlongServiceCalc() {
        return mstrServiceCalc;
    }

    public void setMlongServiceCalc(String mstrServiceCalc) {
        this.mstrServiceCalc = mstrServiceCalc;
    }

    @IndexColumn
    public int getMintIndex() {
        return mintIndex;
    }

    public void setMintIndex(int mintIndex) {
        this.mintIndex = mintIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceExcel that = (ServiceExcel) o;
        return mintIndex == that.mintIndex &&
                Objects.equals(mstrServiceCode, that.mstrServiceCode) &&
                Objects.equals(mstrServiceName, that.mstrServiceName) &&
                Objects.equals(mstrParentCode, that.mstrParentCode) &&
                Objects.equals(mstrGroupCode, that.mstrGroupCode) &&
                Objects.equals(mstrServiceType, that.mstrServiceType) &&
                Objects.equals(mstrServiceCalc, that.mstrServiceCalc) &&
                Objects.equals(mstrServiceCycle, that.mstrServiceCycle) &&
                Objects.equals(mstrServiceChannel, that.mstrServiceChannel) &&
                Objects.equals(mstrFromDate, that.mstrFromDate) &&
                Objects.equals(mstrToDate, that.mstrToDate) &&
                Objects.equals(mstrUnitCode, that.mstrUnitCode) &&
                Objects.equals(exp, that.exp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mstrServiceCode, mstrServiceName, mstrParentCode, mstrGroupCode, mstrServiceType, mstrServiceCalc, mstrServiceCycle, mstrServiceChannel, mstrFromDate, mstrToDate, mstrUnitCode, exp, mintIndex);
    }
}
