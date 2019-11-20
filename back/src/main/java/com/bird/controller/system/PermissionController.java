package com.bird.controller.system;

import com.bird.model.entity.SystemPermissionNode;
import com.bird.model.vo.PermissionNodeWithRole;
import com.bird.service.system.IPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.bird.utils.ObjectUtil.generateSubclass;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * 前端权限接口
 *
 * @author zhyyy
 **/
@RestController
public class PermissionController extends BaseSystemController {

    @Autowired
    private IPermissionService permissionService;

    @RequestMapping(value = "/permissions", method = GET)
    public Object listAllPermissions(@RequestParam(required = false) String module,
                                     @RequestParam(required = false) String name,
                                     @RequestParam(required = false) String des,
                                     HttpServletRequest request) {
        return responseWrap(() -> {
            assertRoot(request);
            List<SystemPermissionNode> temp = permissionService.list(module, name, des);
            List<PermissionNodeWithRole> result = temp.stream()
                    .map(t -> generateSubclass(t, PermissionNodeWithRole.class))
                    .collect(Collectors.toList());
            result.forEach(r -> r.setRoleIds(roleService.listRoleIdsByPermission(r.getId())));
            return result;
        });
    }

    @RequestMapping(value = "/permission", method = POST)
    public Object addPermissions(@RequestBody SystemPermissionNode toAdd,
                                 HttpServletRequest request) {
        return responseWrap(r -> {
            assertDev();
            assertRoot(request);
            assertEmpty(toAdd.getName(), "权限代码不能为空");
            assertEmpty(toAdd.getModule(), "组件名称不能为空");
            assertEmpty(toAdd.getDes(), "权限描述不能为空");
            permissionService.addPermission(toAdd);
        });
    }

    @RequestMapping(value = "/permission", method = PUT)
    public Object updatePermission(@RequestBody SystemPermissionNode toUpdate) {
        return responseWrap(r -> {
            assertDev();
            assertEmpty(toUpdate.getName(), "权限代码不能为空");
            assertEmpty(toUpdate.getModule(), "组件名称不能为空");
            assertEmpty(toUpdate.getDes(), "权限描述不能为空");
            permissionService.updatePermission(toUpdate);
            permissionService.flush();
        });
    }

    @RequestMapping(value = "/permission/{id}", method = DELETE)
    public Object deletePermission(@PathVariable int id,
                                   HttpServletRequest request) {
        return responseWrap(r -> {
            assertDev();
            assertRoot(request);
            permissionService.deletePermission(id);
        });
    }

    @RequestMapping(value = "/permission/{id}/role", method = PUT)
    public Object updatePermissionRoles(@PathVariable int id,
                                        @RequestBody List<Integer> roleIds,
                                        HttpServletRequest request) {
        return responseWrap(r -> {
            assertRoot(request);
            permissionService.updatePermissionRoles(id, roleIds);
            permissionService.flush();
            roleService.flushPermissionCache();
        });
    }

    @RequestMapping(value = "/permission/flush", method = PUT)
    public Object flushCache(HttpServletRequest request) {
        return responseWrap(r -> {
            assertRoot(request);
            roleService.flushPermissionCache();
            permissionService.flush();
        });
    }

    @RequestMapping(value = "/permission/{mn}", method = GET)
    public Object listPermissionByComponent(@PathVariable("mn") String moduleName) {
        return responseWrap(() -> permissionService.listRoleIdsByModule(moduleName));
    }
}
