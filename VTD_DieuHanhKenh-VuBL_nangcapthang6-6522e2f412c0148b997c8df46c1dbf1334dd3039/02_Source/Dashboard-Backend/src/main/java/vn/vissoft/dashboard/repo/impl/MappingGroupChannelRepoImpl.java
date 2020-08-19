package vn.vissoft.dashboard.repo.impl;

import vn.vissoft.dashboard.dto.MappingGroupChannelDTO;
import vn.vissoft.dashboard.dto.PlanMonthlyDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.VttGroupChannelDTO;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.MappingGroupChannel;
import vn.vissoft.dashboard.model.VttGroupChannel;
import vn.vissoft.dashboard.repo.MappingGroupChannelRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MappingGroupChannelRepoImpl implements MappingGroupChannelRepoCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * tim kiem mapping vtt va vds theo dieu kien
     *
     * @param mappingGroupChannel
     * @return
     * @throws Exception
     * @author DatNT
     * @since 30/12/2019
     */
    @Override
    public List<Object[]> findByCondition(MappingGroupChannelDTO mappingGroupChannel) throws Exception {
        StringBuilder vbdBuilder = new StringBuilder();
        HashMap vhmpMap = new HashMap();
        int vintFirstResult = 0;
        int vintMaxResult = 0;
        if (mappingGroupChannel.getPager() != null) {
            vintFirstResult = (mappingGroupChannel.getPager().getPage() - 1) * mappingGroupChannel.getPager().getPageSize();
            vintMaxResult = mappingGroupChannel.getPager().getPageSize();
        }
        vbdBuilder.append("select mapping_id,group_channel_code,group_channel_name,vds_channel_code,vds_channel_name,status,user,create_date");
        vbdBuilder.append(" from mapping_group_channel");
        vbdBuilder.append(buildSQL(mappingGroupChannel, vhmpMap));
        vbdBuilder.append(" order by mapping_id desc");
        Query query = entityManager.createNativeQuery(vbdBuilder.toString());
        vhmpMap.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        query.setFirstResult(vintFirstResult);
        query.setMaxResults(vintMaxResult);
        List<Object[]> vlstObjects = query.getResultList();
        if (!DataUtil.isNullOrEmpty(vlstObjects))
            return vlstObjects;
        else return null;
    }

    /**
     * dieu kien tim kiem mapping group channel
     *
     * @param mappingGroupChannel
     * @param hashMap
     * @return
     * @author DatNT
     * @since 30/12/2019
     */
    private StringBuilder buildSQL(MappingGroupChannelDTO mappingGroupChannel, HashMap hashMap) {
        StringBuilder vbdBuilder = new StringBuilder();
        vbdBuilder.append(" where 1=1 ");
        if (!DataUtil.isNullOrEmpty(mappingGroupChannel.getGroupChannelCode())) {
            vbdBuilder.append(" and group_channel_code =:groupChannelCode");
            hashMap.put("groupChannelCode", mappingGroupChannel.getGroupChannelCode().trim());
        }
        if (!DataUtil.isNullOrEmpty(mappingGroupChannel.getVdsChannelCode())) {
            vbdBuilder.append(" and vds_channel_code =:vdsChannelCode");
            hashMap.put("vdsChannelCode", mappingGroupChannel.getVdsChannelCode().trim());
        }

        if (!DataUtil.isNullOrEmpty(mappingGroupChannel.getStatus())) {
            vbdBuilder.append(" and status =:status");
            hashMap.put("status", mappingGroupChannel.getStatus().trim());
        }
        return vbdBuilder;
    }

    /**
     * lay ra so ban ghi cua mapping khi tim kiem
     *
     * @param mappingGroupChannelDTO
     * @return BigInteger
     * @throws Exception
     * @author DatNT
     * @since 2019/12
     */
    @Override
    public BigInteger countByCondition(MappingGroupChannelDTO mappingGroupChannelDTO) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select count(*) from mapping_group_channel  ");
        vsbdSqlBuilder.append(buildSQL(mappingGroupChannelDTO, vhmpMapParams));
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return (BigInteger) query.getSingleResult();
    }

    @Override
    public List<VttGroupChannel> getAllNotInMapping(String code) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select * from vtt_group_channel where vtt_group_channel.group_channel_code not in (select c.group_channel_code from mapping_group_channel c)");
        vsbdSqlBuilder.append(" or vtt_group_channel.group_channel_code ='").append(code).append("'").append(" and group_status = '1' ");
        List<Object[]> vlstMappingChannels = entityManager.createNativeQuery(vsbdSqlBuilder.toString()).getResultList();
        List<VttGroupChannel> listvtt = new ArrayList<>();
        if (!DataUtil.isNullOrEmpty(vlstMappingChannels)) {
            for (Object[] object : vlstMappingChannels) {
                VttGroupChannel vttGroupChannel = new VttGroupChannel();
                vttGroupChannel.setGroupId(Long.parseLong(object[0].toString()));
                vttGroupChannel.setGroupChannelCode(object[1].toString());
                vttGroupChannel.setGroupChannelName(object[2].toString());
                listvtt.add(vttGroupChannel);
            }
        }
        return listvtt;
    }
}
