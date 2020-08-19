package vn.vissoft.dashboard.dto.excel;

import vn.vissoft.dashboard.model.WarningReceiveConfig;

import java.util.List;

public class BaseWarningReceive {
    private int mintSumSuccessfulRecord;
    private int mintTotal;
    private String fileName;
    private String mstrMessage;
    private List<WarningReceiveConfig> list;

    public int getMintSumSuccessfulRecord() {
        return mintSumSuccessfulRecord;
    }

    public void setSumSuccessfulRecord(int mintSumSuccessfulRecord) {
        this.mintSumSuccessfulRecord = mintSumSuccessfulRecord;
    }

    public String getMessage() {
        return mstrMessage;
    }

    public void setMessage(String mstrMessage) {
        this.mstrMessage = mstrMessage;
    }

    public List<WarningReceiveConfig> getList() {
        return list;
    }

    public void setList(List<WarningReceiveConfig> list) {
        this.list = list;
    }

    public int getMintTotal() {
        return mintTotal;
    }

    public void setMintTotal(int mintTotal) {
        this.mintTotal = mintTotal;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
