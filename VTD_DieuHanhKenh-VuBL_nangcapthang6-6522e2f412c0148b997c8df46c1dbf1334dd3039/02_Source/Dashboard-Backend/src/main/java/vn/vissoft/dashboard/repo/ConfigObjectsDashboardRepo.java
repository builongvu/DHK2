package vn.vissoft.dashboard.repo;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.vissoft.dashboard.model.ConfigObjectsDashboard;

import java.util.List;

public interface ConfigObjectsDashboardRepo extends JpaRepository<ConfigObjectsDashboard,Long> {

    @Cacheable(value = "allConfigObjectsDashboardCached")
    public List<ConfigObjectsDashboard> findAll();
}
