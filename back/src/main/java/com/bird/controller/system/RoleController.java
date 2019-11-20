package com.bird.controller.system;

import com.bird.model.entity.SystemRole;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * 角色变更接口
 *
 * @author zhyyy
 **/
@RestController
public class RoleController extends BaseSystemController {

    @RequestMapping(value = "/roles", method = GET)
    public Object listAllRoles(@RequestParam(value = "name", required = false) String name,
                               @RequestParam(value = "name_show", required = false) String nameShow,
                               @RequestParam(value = "status", required = false) Short status) {
        return responseWrap(() ->
                roleService.list(name, nameShow, status)
                        .stream()
                        .filter(r -> !"root".equals(r.getName()))
                        .collect(toList()));
    }

    @RequestMapping(value = "/role", method = POST)
    public Object addRole(@RequestBody SystemRole toAdd) {
        return responseWrap(r -> {
            assertEmpty(toAdd.getName(), "角色代码为空！");
            assertEmpty(toAdd.getNameShow(), "角色名称为空！");
            roleService.addRole(toAdd);
        });
    }

    @RequestMapping(value = "/role", method = PUT)
    public Object updateRole(@RequestBody SystemRole toUpdate) {
        return responseWrap(r -> {
            assertEmpty(toUpdate.getId(), "角色ID为空");
            assertEmpty(toUpdate.getName(), "角色代码为空！");
            assertEmpty(toUpdate.getNameShow(), "角色名称为空！");
            roleService.updateRole(toUpdate);
        });
    }

    @RequestMapping(value = "/role/ban/{id}", method = POST)
    public Object transferBan(@PathVariable int id) {
        return responseWrap(r -> roleService.ban(id));
    }

    @RequestMapping(value = "/role/lift/{id}", method = POST)
    public Object transferLift(@PathVariable int id) {
        return responseWrap(r -> roleService.lift(id));
    }

    @RequestMapping(value = "/role/{id}", method = DELETE)
    public Object deleteRole(@PathVariable int id,
                             HttpServletRequest request) {
        return responseWrap(r -> {
            assertRoot(request);
            assertDev();
            roleService.deleteRole(id);
        });
    }
}
