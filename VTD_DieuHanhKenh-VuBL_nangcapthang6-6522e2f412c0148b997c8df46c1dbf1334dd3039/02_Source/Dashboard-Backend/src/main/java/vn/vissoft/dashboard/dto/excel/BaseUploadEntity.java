package vn.vissoft.dashboard.dto.excel;

public class BaseUploadEntity {
    private int mintSumSuccessfulRecord;
    private int mintSumRecord;
    private String mstrResultFileName;
    private String mstrMessage;

    public int getSumSuccessfulRecord() {
        return mintSumSuccessfulRecord;
    }

    public void setSumSuccessfulRecord(int sumSuccessfulRecord) {
        this.mintSumSuccessfulRecord = sumSuccessfulRecord;
    }

    public int getSumRecord() {
        return mintSumRecord;
    }

    public void setSumRecord(int sumRecord) {
        this.mintSumRecord = sumRecord;
    }

    public String getResultFileName() {
        return mstrResultFileName;
    }

    public void setResultFileName(String resultFileName) {
        this.mstrResultFileName = resultFileName;
    }

    public String getMessage() {
        return mstrMessage;
    }

    public void setMessage(String message) {
        this.mstrMessage = message;
    }
}
