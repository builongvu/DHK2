package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.model.VttObject;

import java.util.List;

public interface VttObjectRepoCustom {

    List<VttObject> findActiveVttObject() throws Exception;
}
