package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.model.VttObject;
import vn.vissoft.dashboard.repo.VttObjectRepo;
import vn.vissoft.dashboard.services.VttObjectService;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class VttObjectServiceImpl implements VttObjectService {

    @Autowired
    private VttObjectRepo vttObjectRepo;

    @Override
    public List<VttObject> getActiveVttObject() throws Exception {
        return vttObjectRepo.findActiveVttObject();
    }
}
