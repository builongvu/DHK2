package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.chart.ConfigSingleCardDTO;
import vn.vissoft.dashboard.model.ConfigSingleCard;

import java.util.List;

public interface ConfigSingleCardRepoCustom {

    List<ConfigSingleCard> getActiveCard() throws Exception;

    List<Object[]> getAllOrderById() throws Exception;

    List<String> lstCardDynamic(Integer groupId);

    List<Object[]> searchSingleCard(ConfigSingleCardDTO configSingleCardDTO) throws Exception;

    void updateSingleCard(ConfigSingleCard configSingleCard) throws Exception;

    List<Object[]> checkDuplicate(ConfigSingleCard configSingleCard, String condition) throws Exception;
}
