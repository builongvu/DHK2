package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.VttObject;

@Repository
public interface VttObjectRepo extends JpaRepository<VttObject,Long>,VttObjectRepoCustom {
}
