package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.model.ConfigObjects;

import java.util.List;

public interface ObjectConfigRepoCustom {

    List<ConfigObjects> getAllChild(Long id) throws Exception;
}
