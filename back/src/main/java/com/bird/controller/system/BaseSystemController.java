package com.bird.controller.system;

import com.bird.common.BirdOutException;
import com.bird.config.Config;
import com.bird.controller.BaseController;
import com.bird.model.entity.SystemRole;
import com.bird.service.system.IRoleService;
import com.bird.service.system.ITokenService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author zhyyy
 **/
public class BaseSystemController extends BaseController {
    @Autowired
    protected ITokenService tokenService;
    @Autowired
    protected IRoleService roleService;
    @Autowired
    protected Config config;

    /**
     * 校验是否root用户进行操作
     *
     * @param request 操作请求
     * @throws BirdOutException 不是root用户时抛出
     */
    void assertRoot(HttpServletRequest request) {
        boolean notRoot = roleService.listRolesByStaff(tokenService.map2User(request).getId())
                .stream()
                .map(SystemRole::getName)
                .noneMatch("root"::equals);
        if (notRoot) {
            throw new BirdOutException("无权限!");
        }
    }

    /**
     * 校验是否开发模式
     */
    void assertDev() {
        String dev = "dev";
        if (!Objects.equals(config.active, dev)) {
            throw new BirdOutException("无权限：" + config.active + "");
        }
    }
}
