package com.bird.controller.system;

import com.bird.model.entity.SystemRoute;
import com.bird.service.system.IRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * 路由接口
 *
 * @author zhyyy
 **/
@RestController
public class RouteController extends BaseSystemController {
    @Autowired
    private IRouteService routeService;

    /**
     * 获取所有动态路由（树状），及其权限角色
     *
     * @param request 请求信息
     * @return response
     */
    @RequestMapping(value = "/route/roles", method = RequestMethod.GET)
    public Object listAllMenu(HttpServletRequest request) {
        return responseWrap(() -> {
            assertRoot(request);
            return routeService.listAllRoutesWithRoles();
        });
    }

    /**
     * 刷新路由缓存
     *
     * @param request 请求信息
     * @return response
     */
    @RequestMapping(value = "/route/flush", method = PUT)
    public Object flushCache(HttpServletRequest request) {
        return responseWrap(r -> {
            assertRoot(request);
            routeService.flushRoutes();
        });
    }

    /**
     * 修改路由的用户权限
     *
     * @param id      路由ID
     * @param roleIds 角色ID
     * @param cascade 是否级联子菜单
     * @param request 请求信息
     * @return response
     */
    @RequestMapping(value = "/route/{id}/role", method = PUT)
    public Object updateMenuRoles(@PathVariable int id,
                                  @RequestBody List<Integer> roleIds,
                                  @RequestParam(defaultValue = "false") Boolean cascade,
                                  HttpServletRequest request) {
        return responseWrap(() -> {
            assertRoot(request);
            routeService.updateRoleIds(id, cascade, roleIds);
            return routeService.listAllRoutesWithRoles();
        });
    }

    @RequestMapping(value = "/route", method = POST)
    public Object addRoute(@RequestBody SystemRoute toAdd,
                           HttpServletRequest request) {
        return responseWrap(r -> {
            assertRoot(request);
            assertDev();
            routeService.addRoute(toAdd);
        });
    }

    @RequestMapping(value = "/route", method = PUT)
    public Object updateRoute(@RequestBody SystemRoute toUpdate,
                              HttpServletRequest request) {
        return responseWrap(r -> {
            assertRoot(request);
            assertDev();
            routeService.updateRoute(toUpdate);
        });
    }

    @RequestMapping(value = "/route/{id}", method = DELETE)
    public Object deleteRoute(@PathVariable int id,
                              HttpServletRequest request) {
        return responseWrap(r -> {
            assertRoot(request);
            assertDev();
            routeService.deleteRoute(id);
            routeService.flushRoutes();
        });
    }
}
