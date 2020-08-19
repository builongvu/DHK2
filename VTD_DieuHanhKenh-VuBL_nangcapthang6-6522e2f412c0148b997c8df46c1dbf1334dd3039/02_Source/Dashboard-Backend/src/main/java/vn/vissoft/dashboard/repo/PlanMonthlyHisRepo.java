package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.PlanMonthlyHis;

@Repository
public interface PlanMonthlyHisRepo extends JpaRepository<PlanMonthlyHis, Long>, PlanMonthlyHisCustomRepo {

}
