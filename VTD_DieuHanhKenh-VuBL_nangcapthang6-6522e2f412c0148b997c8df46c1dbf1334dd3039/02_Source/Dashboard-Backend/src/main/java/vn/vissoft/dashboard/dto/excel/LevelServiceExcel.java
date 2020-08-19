package vn.vissoft.dashboard.dto.excel;

import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelColumn;
import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelEntity;
import vn.vissoft.dashboard.helper.excelreader.annotation.IndexColumn;

import java.util.Objects;

@ExcelEntity(dataStartRowIndex = 6, signalConstant = "IMPORT_LEVEL_SERVICES")
public class LevelServiceExcel extends BaseExcelEntity{

    private String cycleCode;
    private String mstrMonth;
    private String mstrUnit;
    private String mstrStaff;
    private String staffName;
    private String mstrServiceCode;
    private String mstrServiceName;
    private Double fSchedule;
    private int mintIndex;

    //dat them
    private String shopName;

    @ExcelColumn(name = "B",nullable = false)
    public String getCycleCode() {
        return cycleCode;
    }

    public void setCycleCode(String cycleCode) {
        this.cycleCode = cycleCode;
    }

    @ExcelColumn(name = "C", nullable = false,regex = "^(((0)[0-9])|((1)[0-2]))(\\/)\\d{4}$")
    public String getMonth() {
        return mstrMonth;
    }

    public void setMonth(String month) {
        this.mstrMonth = month;
    }

    @ExcelColumn(name = "D",nullable = false)
    public String getUnit() {
        return mstrUnit;
    }

    public void setUnit(String unit) {
        this.mstrUnit = unit;
    }

    @ExcelColumn(name = "E",nullable = true)
    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    @ExcelColumn(name = "F",nullable = true)
    public String getStaff() {
        return mstrStaff;
    }

    public void setStaff(String staff) {
        this.mstrStaff = staff;
    }

    @ExcelColumn(name = "G",nullable = true)
    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    @ExcelColumn(name = "H", nullable = false)
    public String getServiceCode() {
        return mstrServiceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.mstrServiceCode = serviceCode;
    }

    @ExcelColumn(name = "I",nullable = true)
    public String getServiceName() {
        return mstrServiceName;
    }

    public void setServiceName(String serviceName) {
        this.mstrServiceName = serviceName;
    }

    @ExcelColumn(name = "J",nullable = true,regex = "^[+]?(([0-9]\\d*))(\\.\\d+)?$")
    public double getSchedule() {
        return fSchedule;
    }

    public void setSchedule(double schedule) {
        this.fSchedule = schedule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LevelServiceExcel that = (LevelServiceExcel) o;
        return Objects.equals(cycleCode, that.cycleCode) &&
                Objects.equals(mstrMonth, that.mstrMonth) &&
                Objects.equals(mstrUnit, that.mstrUnit) &&
                Objects.equals(mstrStaff, that.mstrStaff) &&
                Objects.equals(staffName, that.staffName) &&
                Objects.equals(mstrServiceCode, that.mstrServiceCode) &&
                Objects.equals(mstrServiceName, that.mstrServiceName) &&
                Objects.equals(fSchedule, that.fSchedule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cycleCode, mstrMonth, mstrUnit, mstrStaff, staffName, mstrServiceCode, mstrServiceName, fSchedule);
    }

    @IndexColumn
    public int getIndex() {
        return mintIndex;
    }

    public void setIndex(int index) {
        this.mintIndex = index;
    }
}
