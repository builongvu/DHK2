package vn.vissoft.dashboard.helper.excelreader.exception;

public class InvalidCellValueException extends ExcelReadingException {


    public InvalidCellValueException(String message) {
        super(message);
    }

    public InvalidCellValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCellValueException(Throwable cause) {
        super(cause);
    }

    public InvalidCellValueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
