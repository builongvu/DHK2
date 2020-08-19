package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.model.Cycle;

import java.util.List;

public interface CycleService {

    List<Cycle> getByAssign(int pintAssign) throws Exception;

}
