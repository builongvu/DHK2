package vn.vissoft.dashboard.dto;

public class Sort {

    private String mstrColumn;
    private String mstrType;

    public String getColumn() {
        return mstrColumn;
    }

    public void setColumn(String pstrColumn) {
        this.mstrColumn = pstrColumn;
    }

    public String getType() {
        return mstrType;
    }

    public void setType(String pstrType) {
        this.mstrType = pstrType;
    }

}
