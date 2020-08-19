package vn.vissoft.dashboard.dto.excel;

import vn.vissoft.dashboard.model.WarningSendConfig;
import vn.vissoft.dashboard.services.WarningSendService;

import java.util.List;

public class BaseWarningSend {
    private int mintSumSuccessfulRecord;
    private String filename;
    private int mintTotal;
    private String mstrMessage;
    private List<WarningSendConfig> list;

    public int getSumSuccessfulRecord() {
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

    public List<WarningSendConfig> getList() {
        return list;
    }

    public void setList(List<WarningSendConfig> list) {
        this.list = list;
    }


    public int getMintTotal() {
        return mintTotal;
    }

    public void setMintTotal(int mintTotal) {
        this.mintTotal = mintTotal;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
