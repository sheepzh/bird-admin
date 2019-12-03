package com.bird.service.buz.impl;

import com.bird.common.BirdException;
import com.bird.common.BirdOutException;
import com.bird.dao.StaffMapper;
import com.bird.dao.StaffRoleMapper;
import com.bird.dao.extend.StaffMapperExtend;
import com.bird.model.entity.Staff;
import com.bird.model.entity.StaffExample;
import com.bird.model.entity.StaffRole;
import com.bird.model.entity.StaffRoleExample;
import com.bird.model.vo.StaffWithRoles;
import com.bird.service.buz.IUserService;
import com.bird.utils.EncryptionUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.bird.common.ResultCode.*;
import static org.apache.logging.log4j.util.Strings.trimToNull;

/**
 * @author zhyyy
 **/
@Service
public class UserServiceImpl implements IUserService {
    private final static Logger LOG = Logger.getLogger(UserServiceImpl.class);

    private final static String DEFAULT_PASSWORD = EncryptionUtil.md5("111111");

    @Autowired
    private StaffMapper staffMapper;

    @Autowired
    private StaffRoleMapper staffRoleMapper;

    @Autowired
    private StaffMapperExtend staffMapperExtend;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Staff login(String account, String password) {
        Staff staff = staffMapperExtend.selectByAccount(account);
        if (staff == null) {
            throw new BirdOutException("用户不存在");
        }
        if (!staff.getPassword().equals(password)) {
            throw new BirdOutException("用户密码不正确", PASSWORD_ERROR, PASSWORD_ERROR_MSG);
        }
        if (INVALID == staff.getStatus()) {
            throw new BirdOutException("该账号已失效，请联系管理员");
        } else if (FORBIDDEN == staff.getStatus()) {
            throw new BirdOutException("该账号已被禁用");
        }
        staff.setLastLogin(new Date());
        staffMapper.updateByPrimaryKeySelective(staff);
        LOG.info("用户登录：" + staff.getAccount() + " " + staff.getName());
        return staff;
    }

    @Override
    public void changePassword(String account, String oldPsw, String newPsw) {
        Staff staff = foundByAccount(account);
        assertUser(staff);
        if (oldPsw != null) {
            // 用户修改密码
            if (!Objects.equals(oldPsw, staff.getPassword())) {
                throw new BirdOutException("密码错误", PASSWORD_ERROR, PASSWORD_ERROR_MSG);
            } else {
                staff.setPassword(newPsw);
            }
        } else {
            throw new BirdOutException("缺少旧密码");
        }
        StaffExample example = new StaffExample();
        StaffExample.Criteria criteria = example.createCriteria();
        criteria.andAccountEqualTo(account);
        staffMapper.updateByExampleSelective(staff, example);
    }

    @Override
    public void resetPassword(String account) {
        assertNotRoot(account);
        Staff staff = foundByAccount(account);
        assertUser(staff);
        staff.setPassword(DEFAULT_PASSWORD);
        staffMapper.updateByPrimaryKeySelective(staff);
    }

    @Override
    public Staff foundByAccount(String account) {
        return staffMapperExtend.selectByAccount(account);
    }

    @Override
    public List<Staff> list(String name, String account, List<Integer> roles, Integer status) {
        name = trimToNull(name);
        account = trimToNull(account);
        return staffMapperExtend.select(name, account, roles, status);
    }

    @Override
    public void forbidden(String account) {
        Staff staff = staffMapperExtend.selectByAccount(account);
        staff.setStatus(FORBIDDEN);
        staffMapper.updateByPrimaryKeySelective(staff);
    }

    @Override
    public void lift(String account) {
        Staff staff = staffMapperExtend.selectByAccount(account);
        staff.setStatus(NORMAL);
        staffMapper.updateByPrimaryKeySelective(staff);
    }

    @Override
    public void invalid(String account) {
        assertNotRoot(account);
        Staff staff = staffMapperExtend.selectByAccount(account);
        staff.setStatus(INVALID);
        staffMapper.updateByPrimaryKeySelective(staff);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void createUser(StaffWithRoles user) {
        String account = user.getAccount();
        assertNotRoot(account);
        List<Integer> roles = user.getRoles();

        user.setCreateDate(new Date());
        user.setStatus(NORMAL);
        user.setPassword(DEFAULT_PASSWORD);
        staffMapper.insertSelective(user);
        Staff added = staffMapperExtend.selectByAccount(account);
        roles.forEach(r -> {
            StaffRole staffRole = new StaffRole();
            staffRole.setRoleId(r);
            staffRole.setStaffId(added.getId());
            staffRole.setDes("新建用户");
            staffRoleMapper.insertSelective(staffRole);
        });
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public int updateRoles(String account, List<Integer> roles) {
        assertNotRoot(account);
        Staff staff = staffMapperExtend.selectByAccount(account);
        if (staff == null) {
            throw new BirdOutException("用户不存在");
        }
        // 删除原有角色
        StaffRoleExample example = new StaffRoleExample();
        example.createCriteria().andStaffIdEqualTo(staff.getId());
        staffRoleMapper.deleteByExample(example);
        // 添加新角色
        roles.forEach(r -> {
            StaffRole staffRole = new StaffRole();
            staffRole.setRoleId(r);
            staffRole.setStaffId(staff.getId());
            staffRole.setDes("修改角色");
            staffRoleMapper.insertSelective(staffRole);
        });
        return staff.getId();
    }

    @Override
    public List<Staff> listAllUsersSimple() {
        StaffExample example = new StaffExample();
        example.createCriteria().andAccountNotEqualTo("root");
        List<Staff> result = staffMapper.selectByExample(example);
        result.forEach(s -> {
            s.setPassword(null);
            s.setLastLogin(null);
            s.setAddress(null);
            s.setBirthDate(null);
            s.setEmail(null);
            s.setCreateDate(null);
            s.setUpdateDate(null);
        });
        return result;
    }

    @Override
    public void updateUser(Staff staff) {
        assertNotRoot(staff.getAccount());
        Staff old = staffMapperExtend.selectByAccount(staff.getAccount());
        if (old == null) {
            throw new BirdOutException("你在干啥？");
        }
        if (!old.getPassword().equals(staff.getPassword())) {
            throw new BirdOutException("密码错误");
        }
        staff.setId(old.getId());
        staff.setPassword(null);
        staff.setAccount(null);
        staffMapper.updateByPrimaryKeySelective(staff);
    }

    private void assertUser(Staff user) {
        if (user == null) {
            throw new BirdOutException("用户不存在", USER_NOT_FOUND, USER_NOT_FOUND_MSG);
        }
        if (user.getStatus() != 1) {
            throw new BirdOutException("用户已被禁用", USER_FORBIDDEN, USER_FORBIDDEN_MSG);
        }
    }

    private void assertNotRoot(String account) {
        if ("root".equals(account)) {
            throw new BirdOutException("无权限");
        }
    }
}
