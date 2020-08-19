package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.ConfigSingleChart;

import java.util.List;

@Repository
public interface ConfigSingleChartRepo extends JpaRepository<ConfigSingleChart,Long>,ConfigSingleChartRepoCustom {


    List<ConfigSingleChart> findAllByCardId(Long cardId) throws Exception;

    List<ConfigSingleChart> findByDrillDownObjectId(long plngDrilldown);
}
