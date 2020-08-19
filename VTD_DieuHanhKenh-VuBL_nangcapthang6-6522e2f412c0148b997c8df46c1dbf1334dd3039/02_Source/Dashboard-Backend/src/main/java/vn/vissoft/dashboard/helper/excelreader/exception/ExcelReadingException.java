package vn.vissoft.dashboard.helper.excelreader.exception;

public class ExcelReadingException extends Exception {
    private int row;
    private String columnName;

    public ExcelReadingException(String message) {
        super(message);
    }

    public ExcelReadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExcelReadingException(Throwable cause) {
        super(cause);
    }

    public ExcelReadingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


    public ExcelReadingException() {
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
