package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.model.ActionDetail;

@Repository
@Service
public interface ActionDetailRepo extends JpaRepository<ActionDetail, Long>,ActionDetailRepoCustom {

}
