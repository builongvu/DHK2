package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.VdsGroupChannelDTO;
import vn.vissoft.dashboard.helper.LogicException;
import vn.vissoft.dashboard.model.VdsGroupChannel;

import java.util.List;

public interface ChannelService {

//    public List<VdsGroupChannelDTO> getChannelsByCondition(VdsGroupChannelDTO vdsChannelDTO) throws LogicException, Exception;

    public List<VdsGroupChannel> findAll();

    public List<VdsGroupChannel> getActiveChannel() throws Exception;

    String persist(VdsGroupChannel vdsGroupChannel, StaffDTO staffDTO) throws Exception;

    List<VdsGroupChannel> getActiveGroupChannel() throws Exception;

    List<VdsGroupChannel> getChannelByCondion(String pstrShopCode) throws Exception;

    String getNameByCode(String pstrVdsChannelCode) throws Exception;

}
