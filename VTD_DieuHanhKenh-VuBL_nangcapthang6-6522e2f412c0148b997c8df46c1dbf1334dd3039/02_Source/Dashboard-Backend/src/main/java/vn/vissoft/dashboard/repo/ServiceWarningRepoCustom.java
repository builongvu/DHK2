package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.model.ServiceWarningConfig;

import java.util.List;

public interface ServiceWarningRepoCustom {
    List<ServiceWarningConfig> getWarningLevel(String strChannelCode, Long plngServiceId) throws Exception;
}
