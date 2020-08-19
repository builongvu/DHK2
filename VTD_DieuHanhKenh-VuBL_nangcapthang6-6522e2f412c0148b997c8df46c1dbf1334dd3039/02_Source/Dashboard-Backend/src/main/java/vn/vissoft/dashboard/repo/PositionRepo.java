package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.Position;

@Repository
public interface PositionRepo extends JpaRepository<Position,Long>,PositionRepoCustom {
}
