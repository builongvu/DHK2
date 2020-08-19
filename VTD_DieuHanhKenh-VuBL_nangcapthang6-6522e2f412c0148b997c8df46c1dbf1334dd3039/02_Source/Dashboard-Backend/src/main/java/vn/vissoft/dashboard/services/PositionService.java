package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.model.Position;

import java.util.List;

public interface PositionService {
    List<Position> getAllPosition() throws Exception;
}
