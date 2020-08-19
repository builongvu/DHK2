package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.model.ServiceChannel;

import java.util.List;

public interface ServiceChannelRepoCustom {
    List<ServiceChannel> findByChannelCode(String pstrChannelCode ) throws Exception;
}
