package vn.vissoft.dashboard.dto.chart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.JsonArray;

import java.util.List;

public class TableColumnDTO {
    private String mstrName;
    private String mstrType;
    private String mstrAlign;
    private int mintColumnId;
    private Long serviceId;
    private String value;
    private JsonArray metaData;
    private String staffCode;
    private List<TableColumnDTO> columns;

    public TableColumnDTO() {
    }

    public TableColumnDTO(String mstrName, String mstrType, String mstrAlign, int mintColumnId,String value) {
        this.mstrName = mstrName;
        this.mstrType = mstrType;
        this.mstrAlign = mstrAlign;
        this.mintColumnId = mintColumnId;
        this.value=value;
    }

    public String getName() {
        return mstrName;
    }

    public void setName(String pstrName) {
        this.mstrName = pstrName;
    }

    public String getType() {
        return mstrType;
    }

    public void setType(String pstrType) {
        this.mstrType = pstrType;
    }

    public String getAlign() {
        return mstrAlign;
    }

    public void setAlign(String pstrAlign) {
        this.mstrAlign = pstrAlign;
    }

    public int getColumnId() {
        return mintColumnId;
    }

    public void setColumnId(int pintColumnId) {
        this.mintColumnId = pintColumnId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JsonIgnore
    public JsonArray getMetaData() {
        return metaData;
    }

    public void setMetaData(JsonArray metaData) {
        this.metaData = metaData;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public List<TableColumnDTO> getColumns() {
        return columns;
    }

    public void setColumns(List<TableColumnDTO> columns) {
        this.columns = columns;
    }
}
