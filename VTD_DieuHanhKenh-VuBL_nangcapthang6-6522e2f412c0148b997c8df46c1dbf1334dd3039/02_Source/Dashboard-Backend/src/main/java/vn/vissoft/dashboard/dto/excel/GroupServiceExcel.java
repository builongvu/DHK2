package vn.vissoft.dashboard.dto.excel;


import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelColumn;
import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelEntity;
import vn.vissoft.dashboard.helper.excelreader.annotation.IndexColumn;

import java.util.Objects;

@ExcelEntity(dataStartRowIndex = 6, signalConstant = "IMPORT_GROUP_SERVICES")
public class GroupServiceExcel extends BaseExcelEntity {
    private String productCode;
    private String code;
    private String name;
    private int index;

    @ExcelColumn(name = "B")
    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    @ExcelColumn(name = "C",regex = "^[a-zA-Z0-9_]{1,51}")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @ExcelColumn(name = "D")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        GroupServiceExcel that = (GroupServiceExcel) o;
        return Objects.equals(productCode, that.productCode) &&
                Objects.equals(code, that.code) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productCode, code, name);
    }
}
