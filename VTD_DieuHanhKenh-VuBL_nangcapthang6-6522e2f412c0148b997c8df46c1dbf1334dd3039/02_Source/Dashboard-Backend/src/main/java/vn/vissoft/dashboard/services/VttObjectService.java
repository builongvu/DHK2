package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.model.VttObject;

import java.util.List;

public interface VttObjectService {

    List<VttObject> getActiveVttObject() throws Exception;
}
