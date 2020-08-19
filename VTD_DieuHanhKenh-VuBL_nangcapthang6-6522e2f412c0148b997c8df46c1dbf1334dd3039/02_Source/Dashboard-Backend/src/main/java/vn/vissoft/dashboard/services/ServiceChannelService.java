package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.model.ServiceChannel;

import java.util.List;

public interface ServiceChannelService {

    List<ServiceChannel> findByServiceId(Long idService);

    ServiceChannel createServiceChannel(ServiceChannel serviceChannel);

    void deleteServiceChannelByIDandCode(Long idService, String channelCode, List<ServiceChannel> serviceChannels);

}
