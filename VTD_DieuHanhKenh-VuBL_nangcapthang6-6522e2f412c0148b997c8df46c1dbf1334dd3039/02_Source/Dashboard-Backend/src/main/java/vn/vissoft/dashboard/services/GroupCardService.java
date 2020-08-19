package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.ConfigGroupCardDTO;
import vn.vissoft.dashboard.model.ConfigGroupCard;

import java.util.List;

public interface GroupCardService {

    List<ConfigGroupCard> findAll() throws Exception;

    List<ConfigGroupCardDTO> getByCondition(ConfigGroupCardDTO configGroupCard) throws Exception;

    String addGroupCard(ConfigGroupCardDTO configGroupCard, StaffDTO staffDTO) throws Exception;

    String updateGroupCard(ConfigGroupCardDTO configGroupCard, StaffDTO staffDTO) throws Exception;

    boolean checkGroupHaveCard(int pintGroupId) throws Exception;

    String deleteGroupCard(int pintGroupId, StaffDTO staffDTO) throws Exception;

    List<ConfigGroupCardDTO> getAllGroupCard() throws Exception;

}
