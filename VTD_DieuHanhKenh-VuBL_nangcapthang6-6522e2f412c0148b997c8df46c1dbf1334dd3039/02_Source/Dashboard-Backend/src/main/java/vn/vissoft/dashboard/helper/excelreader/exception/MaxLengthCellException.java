package vn.vissoft.dashboard.helper.excelreader.exception;

public class MaxLengthCellException extends ExcelReadingException{
	 private long maxLength;
	 public MaxLengthCellException(String message) {
	        super(message);
	    }
	public long getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(long maxLength) {
		this.maxLength = maxLength;
	}
	 
}
