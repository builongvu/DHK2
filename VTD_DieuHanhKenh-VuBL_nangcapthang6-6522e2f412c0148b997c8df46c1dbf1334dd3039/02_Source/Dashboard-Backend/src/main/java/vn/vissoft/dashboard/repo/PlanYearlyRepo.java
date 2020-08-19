package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.PlanYearly;

@Repository
public interface PlanYearlyRepo extends JpaRepository<PlanYearly, Long>, PlanYearlyRepoCustom {
}
