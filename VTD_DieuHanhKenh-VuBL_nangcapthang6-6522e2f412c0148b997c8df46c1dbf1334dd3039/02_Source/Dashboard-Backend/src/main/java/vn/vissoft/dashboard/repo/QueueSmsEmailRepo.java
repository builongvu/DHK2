package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.QueueSmsEmail;

@Repository
public interface QueueSmsEmailRepo extends JpaRepository<QueueSmsEmail, Long>, QueueSmsEmailRepoCustom {
}
