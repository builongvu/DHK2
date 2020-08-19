package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.WarningReceiveConfig;

@Repository
public interface WarningReceiveConfigRepo extends JpaRepository<WarningReceiveConfig, Long> {
    WarningReceiveConfig getByMlngId(Long mlngId);
}
