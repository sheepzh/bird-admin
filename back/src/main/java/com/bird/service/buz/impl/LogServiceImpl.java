package com.bird.service.buz.impl;

import com.bird.dao.StaffActionLogMapper;
import com.bird.model.entity.StaffActionLog;
import com.bird.model.entity.StaffActionLogExample;
import com.bird.service.buz.ILogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.bird.utils.StringUtil.isBlank;

/**
 * @author zhyyy
 **/
@Service
public class LogServiceImpl implements ILogService {
    @Autowired
    private StaffActionLogMapper logMapper;

    @Override
    public List<StaffActionLog> list(String userAccount, String uri, String des, Date requestStart, Date requestEnd, Boolean isSuccess, Integer handlingTimeMin, Integer handlingTimeMax) {
        StaffActionLogExample example = new StaffActionLogExample();
        StaffActionLogExample.Criteria criteria = example.createCriteria();
        if (!isBlank(userAccount)) {
            criteria.andStaffAccountLike("%" + userAccount.trim() + "%");
        }
        if (!isBlank(uri)) {
            criteria.andRequestUriLike("%" + uri.trim() + "%");
        }
        if (!isBlank(des)) {
            criteria.andDesLike("%" + des.trim() + "%");
        }
        if (requestStart != null) {
            criteria.andRequestTimeGreaterThanOrEqualTo(requestStart);
        }
        if (requestEnd != null) {
            criteria.andRequestTimeLessThan(requestEnd);
        }
        if (isSuccess != null) {
            criteria.andSuccessEqualTo(isSuccess);
        }
        if (handlingTimeMin != null) {
            criteria.andHandlingTimeGreaterThanOrEqualTo(handlingTimeMin);
        }
        if (handlingTimeMax != null) {
            criteria.andHandlingTimeLessThan(handlingTimeMax);
        }
        example.setOrderByClause("request_time desc");
        return logMapper.selectByExample(example);
    }
}
