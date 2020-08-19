package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.ConfigGroupCard;
import vn.vissoft.dashboard.model.ConfigSingleCard;

import java.util.List;

@Repository
public interface ConfigSingleCardRepo extends JpaRepository<ConfigSingleCard, Long>, ConfigSingleCardRepoCustom {

    List<ConfigSingleCard> findAllByOrderByCardIdDesc() throws Exception;

    List<ConfigSingleCard> findByGroupIdOrDrillDownObjectId(int pintGroupId, int pintDrilldown);

//    List<ConfigSingleCard> findByDrillDownObjectId(int pintDrillDown);

}
