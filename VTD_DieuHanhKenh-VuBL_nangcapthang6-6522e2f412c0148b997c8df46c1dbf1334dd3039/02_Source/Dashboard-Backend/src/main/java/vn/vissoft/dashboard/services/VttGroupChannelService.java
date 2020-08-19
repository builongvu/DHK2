package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.VttGroupChannelDTO;
import vn.vissoft.dashboard.model.VttGroupChannel;

import java.math.BigInteger;
import java.util.List;

public interface VttGroupChannelService {
    List<VttGroupChannel> getActiveVttChannel() throws Exception;

    String persist(VttGroupChannel vttGroupChannel, StaffDTO staffDTO) throws Exception;

    List<VttGroupChannelDTO> getByCondition(VttGroupChannelDTO vttGroupChannel) throws Exception;

    String checkDuplicateVttChannel(String pstrVttChannelCode) throws Exception;

    List<VttGroupChannel> getAllNotInMapping(String code) throws Exception;
}
