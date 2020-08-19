package vn.vissoft.dashboard.dto.chart;

public class TableCellBaseDTO {
    private int mintColumnId;
    private String mstrViewValue;
    private String mstrColor;
    private boolean isBold;

    public TableCellBaseDTO() {

    }

    public TableCellBaseDTO(int mintColumnId, String mstrViewValue, String mstrColor,boolean isBold) {
        this.mintColumnId = mintColumnId;
        this.mstrViewValue = mstrViewValue;
        this.mstrColor = mstrColor;
        this.isBold=isBold;
    }

    public int getColumnId() {
        return mintColumnId;
    }

    public void setColumnId(int pintColumnId) {
        this.mintColumnId = pintColumnId;
    }

    public String getViewValue() {
        return mstrViewValue;
    }

    public void setViewValue(String pstrViewValue) {
        this.mstrViewValue = pstrViewValue;
    }

    public String getColor() {
        return mstrColor;
    }

    public void setColor(String pstrcolor) {
        this.mstrColor = pstrcolor;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean bold) {
        isBold = bold;
    }
}
