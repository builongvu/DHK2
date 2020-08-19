package vn.vissoft.dashboard.dto;

public class ColumnsDTO {

    private int columnId;
    private String title;
    private String type;
    private String align;
    private String value;
    private String[] cycle;

    public int getColumnId() {
        return columnId;
    }

    public void setColumnId(int columnId) {
        this.columnId = columnId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String[] getCycle() {
        return cycle;
    }

    public void setCycle(String[] cycle) {
        this.cycle = cycle;
    }
}
