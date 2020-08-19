package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.PlanQuarterly;

@Repository
public interface PlanQuarterlyRepo extends JpaRepository<PlanQuarterly, Long>, PlanQuarterlyRepoCustom {
}
