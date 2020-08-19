package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.dto.ApParamDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.mapper.BaseMapper;
import vn.vissoft.dashboard.model.ApParam;
import vn.vissoft.dashboard.repo.ApParamRepo;
import vn.vissoft.dashboard.services.ApParamService;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class ApParamServiceImpl implements ApParamService {

    @Autowired
    private ApParamRepo apParamRepo;

    private BaseMapper<ApParam, ApParamDTO> mapper = new BaseMapper<>(ApParam.class, ApParamDTO.class);

    @Override
    public List<ApParamDTO> findByTypeAndStatus(String pstrType, String pstrStatus) {
        return mapper.toDtoBean(apParamRepo.findAllByTypeAndStatusOrderByName(pstrType, pstrStatus));
    }

    @Override
    public List<ApParamDTO> findByType(String pstrType) throws Exception {
        return mapper.toDtoBean(apParamRepo.findByType(pstrType));
    }

    @Override
    public List<ApParam> getAll() {
        return apParamRepo.findAll();
    }

}
