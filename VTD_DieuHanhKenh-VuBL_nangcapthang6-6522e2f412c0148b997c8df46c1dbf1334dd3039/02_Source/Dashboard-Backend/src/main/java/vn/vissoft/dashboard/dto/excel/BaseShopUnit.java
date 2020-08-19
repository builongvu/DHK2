package vn.vissoft.dashboard.dto.excel;


import vn.vissoft.dashboard.model.ShopUnit;

import java.util.List;

public class BaseShopUnit {
    private int SumSuccessfulRecord;
    private int Total;
    private String fileName;
    private String mstrMessage;
    private List<ShopUnit> list;

    public int getSumSuccessfulRecord() {
        return SumSuccessfulRecord;
    }

    public void setSumSuccessfulRecord(int sumSuccessfulRecord) {
        SumSuccessfulRecord = sumSuccessfulRecord;
    }

    public int getTotal() {
        return Total;
    }

    public void setTotal(int total) {
        Total = total;
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

    public List<ShopUnit> getList() {
        return list;
    }

    public void setList(List<ShopUnit> list) {
        this.list = list;
    }
}
