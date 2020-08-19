package vn.vissoft.dashboard.services;

import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.dto.ApParamDTO;
import vn.vissoft.dashboard.model.ApParam;

import java.math.BigInteger;
import java.util.List;
public interface ApParamService {

    public List<ApParamDTO> findByTypeAndStatus(String pstrType, String pstrStatus);
    public List<ApParamDTO> findByType(String pstrType) throws Exception;
    public List<ApParam> getAll();
}
