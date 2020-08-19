package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.model.VttPosition;

public interface VttPositionRepoCustom {
    void persist(VttPosition vttPosition) throws Exception;
}
