package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.UnitDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.model.ActionAudit;
import vn.vissoft.dashboard.model.ActionDetail;
import vn.vissoft.dashboard.model.ShopUnit;
import vn.vissoft.dashboard.model.Unit;
import vn.vissoft.dashboard.repo.ActionDetailRepo;
import vn.vissoft.dashboard.repo.UnitRepo;
import vn.vissoft.dashboard.services.ActionAuditService;
import vn.vissoft.dashboard.services.ActionDetailService;
import vn.vissoft.dashboard.services.UnitService;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class UnitServiceImpl implements UnitService {

    @Autowired
    UnitRepo unitRepo;

    @Autowired
    ActionAuditService actionAuditService;

    @Autowired
    ActionDetailService actionDetailService;

    @Autowired
    ActionDetailRepo actionDetailRepo;

    @Override
    public List<Unit> getAllUnit() {
        return unitRepo.getAllByStatus();
    }

    @Override
    public List<Unit> getAll() {
        return unitRepo.getAllByOrderByName();
    }

    @Override
    public List<Unit> getByCondition(UnitDTO unitDTO) {
        return unitRepo.getByConditon(unitDTO);
    }

    @Override
    public Long countByCondition(UnitDTO unitDTO) {
        return unitRepo.countByCondition(unitDTO);
    }

    @Override
    public void addUnit(Unit unit, StaffDTO staffDTO) throws Exception {
        Unit unit1 = unitRepo.saveAndFlush(unit);
        saveNewAction(unit1, staffDTO);
    }

    private void saveNewAction(Unit unit, StaffDTO staffDTO) {
        ActionAudit actionAudit = actionAuditService.log(Constants.UNIT_TABLE.UNIT, Constants.ACTION_CODE_ADD, staffDTO.getStaffCode(), unit.getId(), staffDTO.getShopCode());
        actionDetailService.createActionDetail(Constants.UNIT_TABLE.UNIT, unit.getId().toString(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.UNIT_TABLE.NAME, unit.getName(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.UNIT_TABLE.RATE, unit.getRate().toString(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.UNIT_TABLE.CODE, unit.getCode(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.UNIT_TABLE.STATUS, unit.getStatus(), actionAudit.getId(), null);
    }

    @Override
    public void updateUnit(Unit unit, StaffDTO staffDTO) throws Exception {
        Optional<Unit> optional = unitRepo.getById(unit.getId());
        if (optional.isPresent()) {
            Unit unit1 = optional.get();
            ActionAudit actionAudit = actionAuditService.log(Constants.UNIT_TABLE.UNIT, Constants.ACTION_CODE_EDIT, staffDTO.getStaffCode(), unit.getId(), staffDTO.getShopCode());
            saveActionDetail(unit1, unit, actionAudit.getId());
        }
    }

    @Override
    public Unit getById(Long id) {
        Optional<Unit> optional = unitRepo.getById(id);
        if(optional.isPresent()){
            return optional.get();
        }
        return new Unit();
    }

    @Override
    public void lockUnlock(String[] id, String status, StaffDTO staffDTO) {
        for (String unitId : id) {
            Unit unit = getById(Long.parseLong(unitId));
            if (!unit.getStatus().equals(status)) {
                unit.setStatus(status);
                unitRepo.save(unit);
                ActionAudit actionAudit = actionAuditService.log(Constants.UNIT_TABLE.UNIT, Constants.ACTION_CODE_EDIT, staffDTO.getStaffCode(), unit.getId(), staffDTO.getShopCode());
                if (Constants.PARAM_STATUS_0.equals(status)) {
                    actionDetailService.createActionDetail(Constants.UNIT_TABLE.STATUS, Constants.PARAM_STATUS_0, actionAudit.getId(), Constants.PARAM_STATUS);
                } else {
                    actionDetailService.createActionDetail(Constants.UNIT_TABLE.STATUS, Constants.PARAM_STATUS, actionAudit.getId(), Constants.PARAM_STATUS_0);
                }
            }
        }
    }

    private void saveActionDetail(Unit oldUnit, Unit newUnit, Long actionId) throws Exception {
        if (!oldUnit.getName().equals(newUnit.getName())) {
            saveToActionDetail(actionId, oldUnit.getName(), newUnit.getName(), Constants.UNIT_TABLE.NAME);
        }

        if (!oldUnit.getRate().equals(newUnit.getRate())) {
            saveToActionDetail(actionId, oldUnit.getRate().toString(), newUnit.getRate().toString(), Constants.UNIT_TABLE.RATE);
        }

        if (!oldUnit.getStatus().equals(newUnit.getStatus())) {
            saveToActionDetail(actionId, oldUnit.getStatus(), newUnit.getStatus(), Constants.UNIT_TABLE.STATUS);
        }
        unitRepo.save(newUnit);
    }

    public void saveToActionDetail(Long actionID, String oldValue, String newValue, String column) throws Exception {
        ActionDetail actionDetail = new ActionDetail();
        actionDetail.setActionAuditId(actionID);
        if (oldValue != null) {
            actionDetail.setOldValue(oldValue);
        }
        if (newValue != null) {
            actionDetail.setNewValue(newValue);
        }
        actionDetail.setColumnName(column);
        actionDetailRepo.save(actionDetail);
    }

}
