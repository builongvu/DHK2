package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.model.Position;

import java.util.List;

public interface PositionRepoCustom {

    List<Position> findAllPosition() throws Exception;

    boolean checkExistedPositionCode(String pstrCode) throws Exception;

    Long findIdByCode(String pstrCode) throws Exception;

    String findNameByCode(String pstrCode) throws Exception;
}
