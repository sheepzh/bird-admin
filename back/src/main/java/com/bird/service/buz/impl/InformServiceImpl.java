package com.bird.service.buz.impl;

import com.bird.common.BirdException;
import com.bird.common.BirdOutException;
import com.bird.common.cache.CacheKeySeed;
import com.bird.common.cache.KeySeedManager;
import com.bird.common.cache.SimpleKvCache;
import com.bird.dao.InformMapper;
import com.bird.dao.extend.AttachmentMapperExtend;
import com.bird.dao.extend.InformMapperExtend;
import com.bird.dao.extend.StaffMapperExtend;
import com.bird.model.entity.Attachment;
import com.bird.model.entity.Inform;
import com.bird.model.vo.InformVo;
import com.bird.service.buz.IInformService;
import com.bird.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.util.Strings.trimToNull;

/**
 * 系统公告服务
 *
 * @author zhyyy
 **/
@Service
public class InformServiceImpl implements IInformService {
    private final CacheKeySeed MEM_FLAG_INFORM_INFO = KeySeedManager.get("INFORM_INFO_");

    @Autowired
    private InformMapper informMapper;
    @Autowired
    private InformMapperExtend extend;
    @Autowired
    private AttachmentMapperExtend attachmentMapperExtend;
    @Autowired
    private StaffMapperExtend staffMapperExtend;
    @Autowired
    private SimpleKvCache kvCache;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void add(Inform inform, int creatorId) {
        inform.setCreateDate(new Date());
        inform.setCreator(creatorId);
        inform.setStatus(NORMAL);
        informMapper.insertSelective(inform);
    }

    @Override
    public List<Inform> querySimpleList(Short status, String title, Integer creatorId, Boolean topFirst, Date startDate, Date endDate) {
        return extend.selectSimple(status, trimToNull(title), creatorId, topFirst, startDate, endDate);
    }

    @Override
    public Inform get(int id) {
        return kvCache.getOrDefault(
                MEM_FLAG_INFORM_INFO.key(id),
                () -> {
                    Inform inform = informMapper.selectByPrimaryKey(id);
                    if (inform == null) {
                        throw new BirdOutException("ID非法");
                    }
                    InformVo result = ObjectUtil.generateSubclass(inform, InformVo.class);
                    String attachment = inform.getAttchmentList();
                    if ((attachment = trimToNull(attachment)) != null) {
                        // 获取附件ID
                        String[] ids = attachment.split(",");
                        // 根据ID获取所有附件名称信息
                        List<String> idList = Arrays.stream(ids).collect(Collectors.toList());
                        List<Attachment> attachments = attachmentMapperExtend.selectNamesByIds(idList);
                        result.setAttachmentsToShow(attachments);
                    }
                    result.setCreatorName(staffMapperExtend.selectNameById(result.getCreator()));
                    return result;
                }
        );
    }

    @Override
    public void topOrNot(int id, boolean isTop) {
        Inform inform = assertExistAndNormal(id);
        inform.setTop(isTop);
        informMapper.updateByPrimaryKeySelective(inform);
    }

    @Override
    public void cancel(int id, Integer staffId) {
        Inform inform = assertExistAndNormal(id);
        inform.setStatus(CANCELED);
        inform.setCanceler(staffId);
        inform.setCancelDate(new Date());
        inform.setTop(false);
        informMapper.updateByPrimaryKeySelective(inform);
    }

    @Override
    public void outdate(int id, Integer staffId) {
        Inform inform = assertExistAndNormal(id);
        inform.setStatus(OUTDATED);
        inform.setOutdateOperator(staffId);
        inform.setOutdateDate(new Date());
        inform.setTop(false);
        informMapper.updateByPrimaryKeySelective(inform);
    }


    /**
     * 校验公告存在且状态为正常
     *
     * @param id 公告ID
     * @return 公告
     */
    private Inform assertExistAndNormal(int id) {
        Inform toCheck = informMapper.selectByPrimaryKey(id);
        if (toCheck == null) {
            throw new BirdException("非法ID");
        }
        if (toCheck.getStatus() != NORMAL) {
            throw new BirdException("状态错误");
        }
        return toCheck;
    }
}