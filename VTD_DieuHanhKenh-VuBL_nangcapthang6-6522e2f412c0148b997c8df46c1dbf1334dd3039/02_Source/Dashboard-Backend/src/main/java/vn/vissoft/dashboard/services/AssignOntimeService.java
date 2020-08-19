package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.model.ApParam;

public interface AssignOntimeService {
    ApParam SaveAndFlush(ApParam apParam);

    void SaveNewActionDetail(ApParam newParam, Long actionId) throws Exception;

    void SaveActionDetail(ApParam oldAp, ApParam newAp, Long actionId) throws Exception;
    ApParam getById(Long id);
}
