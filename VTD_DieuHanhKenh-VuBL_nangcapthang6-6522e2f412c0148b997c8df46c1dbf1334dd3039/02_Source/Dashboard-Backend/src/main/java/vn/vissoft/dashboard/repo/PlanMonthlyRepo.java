package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.PlanMonthly;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface PlanMonthlyRepo extends JpaRepository<PlanMonthly, Long>, PlanMonthlyRepoCustom {

    List<PlanMonthly> getByServiceId(Long serviceId);

}
