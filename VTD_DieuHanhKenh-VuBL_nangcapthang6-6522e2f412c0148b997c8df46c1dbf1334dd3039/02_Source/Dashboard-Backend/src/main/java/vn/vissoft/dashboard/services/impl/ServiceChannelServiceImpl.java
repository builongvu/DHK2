package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.model.ServiceChannel;
import vn.vissoft.dashboard.repo.ServiceChannelRepo;
import vn.vissoft.dashboard.services.ServiceChannelService;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class ServiceChannelServiceImpl implements ServiceChannelService {

    @Autowired
    ServiceChannelRepo serviceChannelRepo;

    @Override
    public List<ServiceChannel> findByServiceId(Long idService) {
        return serviceChannelRepo.findByServiceId(idService);
    }

    @Override
    public void deleteServiceChannelByIDandCode(Long idService, String channelCode, List<ServiceChannel> serviceChannels) {
        for (ServiceChannel channel : serviceChannels) {
            if (channel.getVdsChannelCode().equals(channelCode)) {
                serviceChannelRepo.delete(channel);
            }
        }
    }

    @Override
    public ServiceChannel createServiceChannel(ServiceChannel serviceChannel) {
        return serviceChannelRepo.save(serviceChannel);
    }

}
