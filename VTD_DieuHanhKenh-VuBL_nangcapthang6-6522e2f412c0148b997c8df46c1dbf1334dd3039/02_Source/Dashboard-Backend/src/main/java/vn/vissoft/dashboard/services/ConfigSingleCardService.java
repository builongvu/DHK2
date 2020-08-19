package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleCardDTO;
import vn.vissoft.dashboard.model.ConfigGroupCard;
import vn.vissoft.dashboard.model.ConfigSingleCard;

import java.util.List;

public interface ConfigSingleCardService {

    List<ConfigSingleCard> getActiveCard() throws Exception;

    List<ConfigSingleCardDTO> getAllCard() throws Exception;

    String deleteSingleCard(ConfigSingleCardDTO configSingleCardDTO, StaffDTO staffDTO) throws Exception;

    List<ConfigSingleCardDTO> searchConfigSingleCard(ConfigSingleCardDTO configSingleCardDTO) throws Exception;

    String insertConfigSingleCard(ConfigSingleCardDTO configSingleCardDTO, StaffDTO staffDTO) throws Exception;

    String updateConfigSingleCard(ConfigSingleCard configSingleCard, StaffDTO staffDTO) throws Exception;

    List<ConfigGroupCard> listGroupCard() throws Exception;
}
