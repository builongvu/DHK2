package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.UnitTreeDTO;

import java.util.List;

public interface UnitTreeRepoCustom {

    List<UnitTreeDTO> findAllUnitTrees() throws Exception;

}
