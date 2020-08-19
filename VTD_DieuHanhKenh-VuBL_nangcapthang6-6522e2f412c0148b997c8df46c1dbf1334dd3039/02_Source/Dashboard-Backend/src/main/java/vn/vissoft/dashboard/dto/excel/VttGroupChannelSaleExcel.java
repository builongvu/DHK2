package vn.vissoft.dashboard.dto.excel;

import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelColumn;
import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelEntity;
import vn.vissoft.dashboard.helper.excelreader.annotation.IndexColumn;

import java.util.Objects;

@ExcelEntity(dataStartRowIndex = 6, signalConstant = "IMPORT_VTT_GROUP_CHANNEL_SALE")
public class VttGroupChannelSaleExcel extends BaseExcelEntity {
    private String groupChannelCode;
    private Long channelTypeId;
    private int index;

    @ExcelColumn(name = "B")
    public String getGroupChannelCode() {
        return groupChannelCode;
    }

    public void setGroupChannelCode(String groupChannelCode) {
        this.groupChannelCode = groupChannelCode;
    }

    @ExcelColumn(name = "C", regex = "^[1-9]+$", maxLength = 8)
    public Long getChannelTypeId() {
        return channelTypeId;
    }

    public void setChannelTypeId(Long channelTypeId) {
        this.channelTypeId = channelTypeId;
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
        VttGroupChannelSaleExcel that = (VttGroupChannelSaleExcel) o;
        return Objects.equals(DataUtil.isNullOrEmpty(groupChannelCode) ? "" : groupChannelCode.trim(), DataUtil.isNullOrEmpty(that.groupChannelCode) ? "" : that.groupChannelCode.trim()) &&
                Objects.equals(DataUtil.isNullOrZero(channelTypeId) ? "" : channelTypeId, DataUtil.isNullOrZero(that.channelTypeId) ? "" : that.channelTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupChannelCode, channelTypeId);
    }
}
