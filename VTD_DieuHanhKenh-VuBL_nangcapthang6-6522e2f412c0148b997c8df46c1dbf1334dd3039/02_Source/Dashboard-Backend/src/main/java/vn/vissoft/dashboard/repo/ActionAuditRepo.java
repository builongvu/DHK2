package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.ActionAudit;

@Repository
public interface ActionAuditRepo extends JpaRepository<ActionAudit, Long>,ActionAuditRepoCustom {

}
