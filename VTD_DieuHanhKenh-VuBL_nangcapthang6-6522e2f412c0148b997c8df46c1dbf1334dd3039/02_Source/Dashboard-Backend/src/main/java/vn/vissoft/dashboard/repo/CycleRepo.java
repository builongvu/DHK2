package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.Cycle;

import java.util.List;

@Repository
public interface CycleRepo extends JpaRepository<Cycle, Long>,CycleRepoCustom {

}
