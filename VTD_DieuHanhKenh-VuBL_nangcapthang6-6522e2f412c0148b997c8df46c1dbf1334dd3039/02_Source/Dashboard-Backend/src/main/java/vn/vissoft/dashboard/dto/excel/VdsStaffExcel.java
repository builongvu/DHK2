package vn.vissoft.dashboard.dto.excel;

import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelColumn;
import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelEntity;
import vn.vissoft.dashboard.helper.excelreader.annotation.IndexColumn;

import java.util.Objects;

@ExcelEntity(dataStartRowIndex = 6, signalConstant = "IMPORT_VDS_STAFF")
public class VdsStaffExcel extends BaseExcelEntity{

    private String shopCode;
    private String staffCode;
    private String staffName;
    private String staffType;
    private String phoneNumber;
    private String email;
    private String shopWarning;
    private int index;

    @ExcelColumn(name = "B", nullable = false)
    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    @ExcelColumn(name = "C", nullable = false)
    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    @ExcelColumn(name = "D", nullable = false, maxLength = 100)
    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    @ExcelColumn(name = "E", nullable =  false)
    public String getStaffType() {
        return staffType;
    }

    public void setStaffType(String staffType) {
        this.staffType = staffType;
    }

    @ExcelColumn(name = "F", maxLength = 50)
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @ExcelColumn(name = "G", maxLength = 50)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @ExcelColumn(name = "H", nullable = false)
    public String getShopWarning() {
        return shopWarning;
    }

    public void setShopWarning(String shopWarning) {
        this.shopWarning = shopWarning;
    }

    @IndexColumn
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VdsStaffExcel that = (VdsStaffExcel) o;
        return Objects.equals(shopCode, that.shopCode) &&
                Objects.equals(staffCode, that.staffCode) &&
                Objects.equals(staffName, that.staffName) &&
                Objects.equals(staffType, that.staffType) &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(email, that.email) &&
                Objects.equals(shopWarning, that.shopWarning);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shopCode, staffCode, staffName, staffType, phoneNumber, email, shopCode, shopWarning);
    }
}
