package com.bird.service.buz;

import com.bird.model.entity.Inform;

import java.util.Date;
import java.util.List;

/**
 * 系统公告服务
 *
 * @author zhyyy
 **/
public interface IInformService {

    /**
     * 状态：已撤销
     */
    short CANCELED = 0;
    /**
     * 状态：正常
     */
    short NORMAL = 1;
    /**
     * 状态：已过期
     */
    short OUTDATED = 2;

    /**
     * 添加公告
     *
     * @param inform    公告信息
     * @param creatorId 创建人ID
     */
    void add(Inform inform, int creatorId);

    /**
     * 获取公告列表简单信息
     *
     * @param status    状态
     * @param title     标题，模糊查询
     * @param creatorId 创建人ID
     * @param topFirst  是否优先查询置顶公告
     * @param startDate 发布日期起始
     * @param endDate   发布日期结束
     * @return 列表
     */
    List<Inform> querySimpleList(Short status, String title, Integer creatorId, Boolean topFirst, Date startDate, Date endDate);

    /**
     * 按主键获取
     *
     * @param id ID
     * @return 详细信息
     */
    Inform get(int id);


    /**
     * 修改置顶状态
     *
     * @param id    公告ID
     * @param isTop 是否置顶
     */
    void topOrNot(int id, boolean isTop);

    /**
     * 撤销公告
     *
     * @param id      ID
     * @param staffId 操作人ID
     */
    void cancel(int id, Integer staffId);

    /**
     * 使公告过期
     *
     * @param id      ID
     * @param staffId 操作人ID
     */
    void outdate(int id, Integer staffId);
}
