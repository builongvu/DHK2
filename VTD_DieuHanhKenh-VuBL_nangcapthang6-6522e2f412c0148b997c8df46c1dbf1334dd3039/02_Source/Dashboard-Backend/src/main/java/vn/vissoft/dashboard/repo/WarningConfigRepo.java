package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.WarningConfig;

import java.util.List;
@Repository
public interface WarningConfigRepo extends JpaRepository<WarningConfig,Long>, WarningConfigRepoCustom {
//    List<WarningConfig> findAllById(Long idWarning);
    List<WarningConfig> findAllBySvID(Long SvId);
}
