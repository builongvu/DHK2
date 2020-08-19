package vn.vissoft.dashboard.dto.chart;

import java.util.List;

public class TopDataDTO{

    private List<String> mstrData;
    private int mintTopNum;
    private boolean mblnIsBest;
    private String mstrColor;
    private String mstrName;

    public String getName() {
        return mstrName;
    }

    public void setName(String mstrName) {
        this.mstrName = mstrName;
    }

    public int getTopNum() {
        return mintTopNum;
    }

    public void setTopNum(int pintTopNum) {
        this.mintTopNum = pintTopNum;
    }

    public boolean getIsBest() {
        return mblnIsBest;
    }

    public void setIsBest(boolean pblnBest) {
        mblnIsBest = pblnBest;
    }

    public String getColor() {
        return mstrColor;
    }

    public void setColor(String pstrColor) {
        this.mstrColor = pstrColor;
    }


    public List<String> getData() {
        return mstrData;
    }

    public void setData(List<String> mstrData) {
        this.mstrData = mstrData;
    }
}
