package vn.vissoft.dashboard.dto.excel;

import vn.vissoft.dashboard.model.GroupService;

import java.util.List;

public class BaseUploadGroupEntity {
    private int mintSumSuccessfulRecord;
    private int mintTotal;
    private String fileName;
    private String mstrMessage;
    private List<GroupService> list;

    public int getMintSumSuccessfulRecord() {
        return mintSumSuccessfulRecord;
    }

    public void setMintSumSuccessfulRecord(int mintSumSuccessfulRecord) {
        this.mintSumSuccessfulRecord = mintSumSuccessfulRecord;
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

    public String getMstrMessage() {
        return mstrMessage;
    }

    public void setMstrMessage(String mstrMessage) {
        this.mstrMessage = mstrMessage;
    }

    public List<GroupService> getList() {
        return list;
    }

    public void setList(List<GroupService> list) {
        this.list = list;
    }
}
