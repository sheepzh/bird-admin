package com.bird.controller.buz;

import com.bird.common.BirdOutException;
import com.bird.common.PageInfo;
import com.bird.common.ResultCode;
import com.bird.controller.BaseController;
import com.bird.controller.UserLog;
import com.bird.model.entity.Staff;
import com.bird.model.entity.SystemRole;
import com.bird.model.entity.SystemRoute;
import com.bird.model.vo.StaffWithRoles;
import com.bird.service.buz.IUserService;
import com.bird.service.system.IRoleService;
import com.bird.service.system.IRouteService;
import com.bird.service.system.ITokenService;
import com.bird.utils.ObjectUtil;
import com.bird.utils.WebUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * @author zhyyy
 **/
@RestController
public class UserController extends BaseController {

    private final IUserService userService;
    private final IRoleService roleService;
    private final ITokenService tokenService;
    private final IRouteService routeService;

    @Autowired
    public UserController(IUserService userService, IRoleService roleService, ITokenService tokenService, IRouteService routeService) {
        this.userService = userService;
        this.roleService = roleService;
        this.tokenService = tokenService;
        this.routeService = routeService;
    }

    @UserLog(value = "用户登录", noToken = true)
    @RequestMapping(value = "/user/login", method = POST)
    public Object login(HttpServletRequest request,
                        @RequestBody Map<String, String> staffMap) {
        return responseWrap(r -> {
            String account = staffMap.get("ac");
            String password = staffMap.get("ps");
            Staff user = userService.login(account, password);
            String ip = WebUtil.getIpAddress(request);
            r.set("token", tokenService.login(user, ip));
            // 密码不对外展示
            user.setPassword(null);
            List<String> roles = roleService.listRolesByStaff(user.getId()).stream().map(SystemRole::getName).collect(Collectors.toList());
            r.set("user", user);
            r.set("roles", roles);
        });
    }

    @UserLog("注销登录")
    @RequestMapping(value = "/user/logout", method = POST)
    public Object logout(HttpServletRequest request) {
        return responseWrap(r -> tokenService.logout(WebUtil.getToken(request)));
    }

    /**
     * 重置密码，仅限admin用户操作
     *
     * @param account 待重置用户的账号
     * @param request 请求信息
     * @return response
     */
    @UserLog("重置密码")
    @RequestMapping(value = "/user/{ac}/reset", method = PUT)
    public Object reset(@PathVariable("ac") String account,
                        HttpServletRequest request) {
        return responseWrap(r -> {
            assertAdmin(request);
            userService.resetPassword(account);
        });
    }

    /**
     * 修改密码，仅限自己操作
     *
     * @param account 账号
     * @param oldPsw  旧密码
     * @param newPsw  新密码
     * @param request 请求信息
     * @return response
     */
    @UserLog("修改密码")
    @RequestMapping(value = "/user/{ac}/change", method = PUT)
    public Object changePassword(@PathVariable("ac") String account,
                                 @RequestParam("op") String oldPsw,
                                 @RequestParam("np") String newPsw,
                                 HttpServletRequest request) {
        return responseWrap(r -> {
            assertSelf(request, account);
            userService.changePassword(account, oldPsw, newPsw);
        });
    }

    @RequestMapping(value = "/user/info", method = GET)
    public Object userInfo(HttpServletRequest request) {
        return responseWrap(r -> {
            Staff user = tokenService.map2User(request);
            if (user != null) {
                user.setPassword(null);
                List<SystemRole> roles = roleService.listRolesByStaff(user.getId());
                List<String> routes = routeService.listByRoles(roles.stream()
                        .map(SystemRole::getId).collect(Collectors.toList()))
                        .stream().map(SystemRoute::getPath).collect(Collectors.toList());
                r.set("roles", roles.stream().map(SystemRole::getName).collect(Collectors.toList()));
                r.set("routes", routes);
                r.set("user", user);
            } else {
                r.fail(ResultCode.LOGIN_TIMEOUT_ERROR, ResultCode.LOGIN_TIMEOUT_MSG);
            }
        });
    }

    @RequestMapping(value = "/users", method = GET)
    public Object listUser(@RequestParam(required = false, value = "name") String name,
                           @RequestParam(required = false, value = "account") String account,
                           @RequestParam(required = false, value = "roles") List<Integer> roles,
                           @RequestParam(required = false, value = "status") Integer status,
                           @RequestParam(defaultValue = "10", value = "limit") Integer pageSize,
                           @RequestParam(defaultValue = "1", value = "page") Integer pageNum) {
        return responseWrap(() -> {
            // 一定将以下两行代码连续写
            PageHelper.startPage(pageNum, pageSize);
            PageInfo<Staff> pageInfo = new PageInfo<>(userService.list(name, account, roles, status));

            List<Staff> staffList = pageInfo.getList();
            List<StaffWithRoles> dto = staffList.stream()
                    .map(s -> ObjectUtil.generateSubclass(s, StaffWithRoles.class))
                    .collect(Collectors.toList());
            dto.forEach(d -> {
                d.setPassword(null);
                d.setRoles(roleService.listRoleIdsByStaff(d.getId()));
            });
            pageInfo.setList(dto);
            return pageInfo;
        });
    }

    @UserLog("新建用户")
    @RequestMapping(value = "/user", method = POST)
    public Object createUser(@RequestBody StaffWithRoles user) {
        return responseWrap(r -> userService.createUser(user));
    }

    @RequestMapping(value = "/user/account/{ac}", method = GET)
    public Object getUserByAccount(@PathVariable("ac") String account) {
        return responseWrap(() -> userService.foundByAccount(account));
    }

    @UserLog("配置用户角色")
    @RequestMapping(value = "/user/{ac}/role", method = PUT)
    public Object updateUserRoles(@PathVariable("ac") String account,
                                  @RequestBody List<Integer> roles) {
        return responseWrap(r -> {
            int userId = userService.updateRoles(account, roles);
            roleService.flushUserRole(userId);
        });
    }

    @UserLog("禁用用户")
    @RequestMapping(value = "/user/{ac}/forbidden", method = PUT)
    public Object forbidden(@PathVariable("ac") String account,
                            HttpServletRequest request) {
        return responseWrap(r -> {
            String operatorAccount = assertAdmin(request);
            if (Objects.equals(account, operatorAccount)) {
                throw new BirdOutException("不可禁用自身");
            }
            // 禁用
            userService.forbidden(account);
            // 登出用户
            tokenService.logoutByAccount(account);
        });
    }

    /**
     * 解禁用户
     *
     * @param account 账号
     * @param request 请求信息
     * @return response
     */
    @UserLog("解禁用户")
    @RequestMapping(value = "/user/{ac}/lift", method = PUT)
    public Object lift(@PathVariable("ac") String account,
                       HttpServletRequest request) {
        return responseWrap(r -> {
            assertAdmin(request);
            userService.lift(account);
        });
    }

    @UserLog("用户离职")
    @RequestMapping(value = "/user/invalid", method = PUT)
    public Object invalid(@RequestParam("ac") String account,
                          HttpServletRequest request) {
        return responseWrap(r -> {
            assertAdmin(request);
            userService.invalid(account);
        });
    }

    @RequestMapping(value = "/user/simple", method = GET)
    public Object listSimple() {
        return responseWrap(userService::listAllUsersSimple);
    }


    @UserLog("修改用户信息")
    @RequestMapping(value = "/user", method = PUT)
    @Transactional(rollbackFor = Throwable.class)
    public Object update(@RequestBody StaffWithRoles dto) {
        return responseWrap(r -> {
            // 先判断是否需要修改密码
            if (dto.getNewPassword() != null) {
                userService.changePassword(dto.getAccount(), dto.getPassword(), dto.getNewPassword());
                dto.setPassword(dto.getNewPassword());
            }
            userService.updateUser(dto);
        });
    }

    /**
     * 是否管理员
     *
     * @param request 请求信息
     * @return 账号
     */
    private String assertAdmin(HttpServletRequest request) {
        Staff user = tokenService.map2User(request);
        if (user == null || user.getId() == null ||
                roleService.listRolesByStaff(user.getId()).stream().map(SystemRole::getName).noneMatch("admin"::equals)) {
            throw new BirdOutException("没有操作权限");
        }
        return user.getAccount();
    }

    /**
     * 是否本人操作
     *
     * @param request 请求信息
     * @param account 被操作者账号
     */
    private void assertSelf(HttpServletRequest request, String account) {
        Staff user = tokenService.map2User(request);
        if (user == null || !account.equals(user.getAccount())) {
            throw new BirdOutException("非本人操作");
        }
    }
}
