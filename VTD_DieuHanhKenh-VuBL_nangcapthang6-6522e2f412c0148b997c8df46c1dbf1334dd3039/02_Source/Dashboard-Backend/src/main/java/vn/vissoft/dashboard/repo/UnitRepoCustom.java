package vn.vissoft.dashboard.repo;

import org.springframework.data.repository.query.Param;
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.UnitDTO;
import vn.vissoft.dashboard.model.ManageInfoPartner;
import vn.vissoft.dashboard.model.Unit;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface UnitRepoCustom {

//    Long findUnitIdByCode(String pstrCode);
//
//    boolean checkExistedUnitCode(String pstrCode);
//
//    List<ManageInfoPartner> findActiveUnit();

    BigInteger totalUnit();

    String findNameByUnitId(Long plngUnitId);

    List<Unit> getAllByStatus();

    Optional<Unit> getUnitByCode(String code);

    List<Unit> getByConditon(UnitDTO unitDTO);
    Long countByCondition(UnitDTO unitDTO);

}
