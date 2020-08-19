package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.WarningSendConfig;

import java.util.List;

@Repository
public interface WarningSendConfigRepo extends JpaRepository<WarningSendConfig, Long> {
}
