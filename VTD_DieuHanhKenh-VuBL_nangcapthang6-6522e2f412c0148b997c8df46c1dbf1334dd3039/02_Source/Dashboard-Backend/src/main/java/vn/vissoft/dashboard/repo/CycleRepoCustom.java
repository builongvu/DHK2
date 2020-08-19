package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.model.Cycle;

import java.util.List;

public interface CycleRepoCustom {
    List<Cycle> findByAssign(int pintAssign) throws Exception;
    boolean checkExistedCycleCode(String pstrCode)  throws Exception;
}
