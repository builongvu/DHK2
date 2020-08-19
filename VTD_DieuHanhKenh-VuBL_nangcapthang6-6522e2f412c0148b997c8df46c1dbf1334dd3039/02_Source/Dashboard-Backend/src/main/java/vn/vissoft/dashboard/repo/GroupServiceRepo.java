package vn.vissoft.dashboard.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.model.GroupService;
import java.util.List;

@Repository
public interface GroupServiceRepo extends JpaRepository<GroupService, Long>, GroupServiceRepoCustom {

    GroupService getById(Long Id);

    List<GroupService> findAllByStatusNotLikeOrderByName(String status);

    List<GroupService> findByProductIdAndStatus(Long plngPrdId, String pstrStatus);

    GroupService findByCode(String pstrCode);

}
