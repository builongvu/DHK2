package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.model.Position;
import vn.vissoft.dashboard.repo.PositionRepo;
import vn.vissoft.dashboard.services.PositionService;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class PositionServiceImpl implements PositionService {

    @Autowired
    private PositionRepo positionRepo;

    @Override
    public List<Position> getAllPosition() throws Exception {
        return positionRepo.findAllPosition();
    }
}
