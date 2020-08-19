package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.ConfigGroupCard;

import java.util.List;

@Repository
public interface ConfigGroupCardRepo extends JpaRepository<ConfigGroupCard, Long>, ConfigGroupCardRepoCustom {

    ConfigGroupCard findByGroupId(Long plngGroupId);

    List<ConfigGroupCard> findAllByOrderByGroupIdDesc();

    String deleteByGroupId(Long plngGroupId);

}
