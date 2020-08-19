package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.AccessLogin;

@Repository
public interface AccessLoginRepo extends JpaRepository<AccessLogin, Long>, AccessLoginRepoCustom {
}
