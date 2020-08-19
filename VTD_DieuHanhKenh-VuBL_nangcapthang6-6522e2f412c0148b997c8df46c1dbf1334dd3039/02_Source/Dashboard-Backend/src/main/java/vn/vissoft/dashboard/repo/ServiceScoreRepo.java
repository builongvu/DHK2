package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.dto.ServiceScoreDTO;
import vn.vissoft.dashboard.model.ServiceScore;

import java.util.List;

@Repository
public interface ServiceScoreRepo extends JpaRepository<ServiceScore,Long>,ServiceScoreRepoCustom {

}
