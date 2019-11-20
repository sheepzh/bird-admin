package com.bird.service.buz;

import com.bird.model.entity.StaffActionLog;

import java.util.Date;
import java.util.List;

/**
 * 用户操作日志服务
 *
 * @author zhyyy
 * @see com.bird.service.buz.impl.LogServiceImpl
 **/
public interface ILogService {

    /**
     * 条件查询
     *
     * @param userAccount     操作人账号，模糊查询
     * @param des             操作描述
     * @param uri             请求uri
     * @param requestStart    请求时间区间开始
     * @param requestEnd      请求时间区间结束
     * @param isSuccess       是否请求成功
     * @param handlingTimeMin 请求处理时间下限
     * @param handlingTimeMax 请求处理时间上限
     * @return 日志信息
     */
    List<StaffActionLog> list(String userAccount, String des, String uri, Date requestStart, Date requestEnd, Boolean isSuccess,
                              Integer handlingTimeMin, Integer handlingTimeMax);
}
