package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.model.ActionDetail;
import vn.vissoft.dashboard.model.ApParam;
import vn.vissoft.dashboard.repo.ActionDetailRepo;
import vn.vissoft.dashboard.repo.ApParamRepo;
import vn.vissoft.dashboard.repo.WarningConfigRepo;
import vn.vissoft.dashboard.services.AssignOntimeService;

import javax.transaction.Transactional;

@Transactional
@Service
public class AssignOntimeServiceImpl implements AssignOntimeService {

    @Autowired
    private ApParamRepo apParamRepo;

    @Autowired
    private ActionDetailRepo actionDetailRepo;
    @Autowired
    private WarningConfigRepo warningConfigCustomRepo;


    /**
     * Lưu và trả về ApParam
     *
     * @return ApParam
     * @param apParam
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public ApParam SaveAndFlush(ApParam apParam) {
        return apParamRepo.saveAndFlush(apParam);
    }


    /**
     * Lấy ApParam theo id
     *
     * @return ApParam
     * @param id
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public ApParam getById(Long id) {
        return warningConfigCustomRepo.getById(id);
    }

    /**
     * Lưu vào bảng Action Detail
     *
     * @param newParam
     * @param actionId
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    public void SaveNewActionDetail(ApParam newParam, Long actionId) throws Exception {
        saveActionDetail(actionId, "", newParam.getValue(), Constants.APPARAM.VALUE);
        saveActionDetail(actionId, "", newParam.getCode(), Constants.APPARAM.CODE);
        saveActionDetail(actionId, "", newParam.getDescription(), Constants.APPARAM.DES);
        saveActionDetail(actionId, "", newParam.getName(), Constants.APPARAM.NAME);
        saveActionDetail(actionId, "", newParam.getStatus(), Constants.APPARAM.STATUS);
        saveActionDetail(actionId, "", newParam.getType(), Constants.APPARAM.TYPE);
    }


    public void SaveActionDetail(ApParam oldAp, ApParam newAp, Long actionId) throws Exception {
        saveActionDetail(actionId, oldAp.getValue(), newAp.getValue(), Constants.APPARAM.VALUE);
        apParamRepo.save(newAp);
    }
    public void saveActionDetail(Long actionID, String oldValue, String newValue, String column) {
        ActionDetail actionDetail = new ActionDetail();
        actionDetail.setActionAuditId(actionID);
        actionDetail.setOldValue(oldValue);
        actionDetail.setNewValue(newValue);
        actionDetail.setColumnName(column);
        actionDetailRepo.save(actionDetail);
    }
}
