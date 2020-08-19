package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.dto.UnitTreeDTO;
import vn.vissoft.dashboard.repo.UnitTreeRepoCustom;
import vn.vissoft.dashboard.services.UnitTreeService;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class UnitTreeServiceImpl implements UnitTreeService {

    @Autowired
    private UnitTreeRepoCustom unitTreeRepoCustom;

    @Override
    public List<UnitTreeDTO> getAllUnitTrees() throws Exception {
        return unitTreeRepoCustom.findAllUnitTrees();
    }
}
