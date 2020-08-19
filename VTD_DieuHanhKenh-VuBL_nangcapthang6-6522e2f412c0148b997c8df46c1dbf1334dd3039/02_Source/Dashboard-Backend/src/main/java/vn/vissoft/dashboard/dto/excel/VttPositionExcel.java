package vn.vissoft.dashboard.dto.excel;


import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelColumn;
import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelEntity;
import vn.vissoft.dashboard.helper.excelreader.annotation.IndexColumn;

import java.util.Objects;

@ExcelEntity(dataStartRowIndex = 6, signalConstant = "IMPORT_VTT_POSITION")
public class VttPositionExcel extends BaseExcelEntity {
    private String groupChannelCode;
    private String positionCode;
    private int index;

    @ExcelColumn(name = "B")
    public String getGroupChannelCode() {
        return groupChannelCode;
    }

    public void setGroupChannelCode(String groupChannelCode) {
        this.groupChannelCode = groupChannelCode;
    }

    @ExcelColumn(name = "C")
    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
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
        VttPositionExcel that = (VttPositionExcel) o;
        return Objects.equals(DataUtil.isNullOrEmpty(groupChannelCode) ? "" : groupChannelCode.trim(), DataUtil.isNullOrEmpty(that.groupChannelCode) ? "" : that.groupChannelCode.trim()) &&
                Objects.equals(DataUtil.isNullOrEmpty(positionCode) ? "" : positionCode.trim(), DataUtil.isNullOrEmpty(that.positionCode) ? "" : that.positionCode.trim());
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupChannelCode, positionCode);
    }
}
