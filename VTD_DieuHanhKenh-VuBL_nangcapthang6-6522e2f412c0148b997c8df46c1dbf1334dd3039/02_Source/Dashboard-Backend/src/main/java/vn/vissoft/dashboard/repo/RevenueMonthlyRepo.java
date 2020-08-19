package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.RevenueMonthly;

@Repository
public interface RevenueMonthlyRepo extends JpaRepository<RevenueMonthly, Long> {

}
