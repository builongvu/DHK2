package vn.vissoft.dashboard.dto.chart;

import java.util.List;

public class ListTopByServiceDTO extends ContentBaseDTO {

    private List<TopByServiceDTO> lstData;

    public List<TopByServiceDTO> getLstData() {
        return lstData;
    }

    public void setLstData(List<TopByServiceDTO> lstData) {
        this.lstData = lstData;
    }
}
