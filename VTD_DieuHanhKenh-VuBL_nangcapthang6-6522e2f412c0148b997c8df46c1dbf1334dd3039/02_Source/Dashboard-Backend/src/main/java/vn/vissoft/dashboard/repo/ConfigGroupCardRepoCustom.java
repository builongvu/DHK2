package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.chart.ConfigGroupCardDTO;
import vn.vissoft.dashboard.model.ConfigGroupCard;

import java.util.List;

public interface ConfigGroupCardRepoCustom {

    List<Object[]> findByCondition(ConfigGroupCardDTO configGroupCard) throws Exception;

    List<Object[]> findAllGroupCard() throws Exception;

    List<Object[]> getAllOrderByNameAsc() throws Exception;

}
