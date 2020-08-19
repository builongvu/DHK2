package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.UnitDTO;
import vn.vissoft.dashboard.model.Unit;

import java.math.BigInteger;
import java.util.List;

public interface UnitService {

    List<Unit> getAllUnit();

    List<Unit> getAll();

    List<Unit> getByCondition(UnitDTO unitDTO);

    Long countByCondition(UnitDTO unitDTO);

    void addUnit(Unit unit, StaffDTO staffDTO) throws Exception;

    void updateUnit(Unit unit, StaffDTO staffDTO) throws Exception;

    Unit getById(Long id);

    void lockUnlock(String[] id, String status, StaffDTO staffDTO);

}
