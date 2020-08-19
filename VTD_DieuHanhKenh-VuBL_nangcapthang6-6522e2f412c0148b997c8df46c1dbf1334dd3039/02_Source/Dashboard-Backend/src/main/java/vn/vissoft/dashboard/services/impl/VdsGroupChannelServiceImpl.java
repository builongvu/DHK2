package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.model.VdsGroupChannel;
import vn.vissoft.dashboard.repo.VdsGroupChannelRepo;
import vn.vissoft.dashboard.services.VdsGroupChannelService;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class VdsGroupChannelServiceImpl implements VdsGroupChannelService {

    @Autowired
    private VdsGroupChannelRepo vdsGroupChannelRepo;

    @Override
    public List<VdsGroupChannel> getAll() {
        return vdsGroupChannelRepo.findAll();
    }
}
