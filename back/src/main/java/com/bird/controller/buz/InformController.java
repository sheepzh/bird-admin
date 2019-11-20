package com.bird.controller.buz;

import com.bird.common.PageInfo;
import com.bird.controller.BaseController;
import com.bird.controller.UserLog;
import com.bird.model.entity.Inform;
import com.bird.model.entity.Staff;
import com.bird.model.vo.InformVo;
import com.bird.service.buz.IInformService;
import com.bird.service.system.ITokenService;
import com.bird.utils.DateUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * @author zhyyy
 **/
@RestController
public class InformController extends BaseController {

    @Autowired
    private ITokenService tokenService;
    @Autowired
    private IInformService informService;

    @UserLog("新增公告")
    @RequestMapping(value = "/inform", method = POST)
    public Object addInform(@RequestBody InformVo inform,
                            HttpServletRequest request) {
        return responseWrap(r -> {
            assertEmpty(inform.getTitle(), "标题不能为空");
            assertEmpty(inform.getContent(), "内容不能为空");
            Staff creator = tokenService.map2User(request);
            List<Integer> attachmentIds = inform.getAttachments();
            if (attachmentIds != null) {
                String idList = attachmentIds.stream().map(String::valueOf).collect(Collectors.joining(","));
                inform.setAttchmentList(idList);
            }
            informService.add(inform, creator.getId());
        });
    }

    @UserLog(value = "分页查询公告", need = false)
    @RequestMapping(value = "/informs", method = GET)
    public Object get(@RequestParam(required = false) Short status,
                      @RequestParam(required = false) String title,
                      @RequestParam(required = false, value = "creator") Integer creatorId,
                      @RequestParam(value = "sd", required = false) Long startDate,
                      @RequestParam(value = "ed", required = false) Long endDate,
                      @RequestParam(value = "tf", required = false) Boolean topFirst,
                      @RequestParam(defaultValue = "1") int page,
                      @RequestParam(defaultValue = "10") int limit) {
        return responseWrap(() -> {
            Date startOfCreate = DateUtil.startOfThisDay(startDate);
            Date endOfCreate = DateUtil.startOfNextDay(endDate);
            PageHelper.startPage(page, limit);
            return new PageInfo<>(informService.querySimpleList(status, title, creatorId, topFirst, startOfCreate, endOfCreate));
        });
    }

    @UserLog("查看公告详情")
    @RequestMapping(value = "/inform/{id}", method = GET)
    public Object get(@PathVariable int id) {
        return responseWrap(() -> {
            Inform inform = informService.get(id);
            if (Objects.equals(inform.getStatus(), IInformService.CANCELED)) {
                inform.setContent(null);
            }
            inform.setOutdateDate(null);
            inform.setOutdateOperator(null);
            inform.setCancelDate(null);
            inform.setCanceler(null);
            return inform;
        });
    }

    @UserLog("置顶公告")
    @RequestMapping(value = "/inform/{id}/top", method = PUT)
    public Object top(@PathVariable int id) {
        return responseWrap(r -> informService.topOrNot(id, true));
    }

    @UserLog("取消公告的置顶")
    @RequestMapping(value = "/inform/{id}/untop", method = PUT)
    public Object untop(@PathVariable int id) {
        return responseWrap(r -> informService.topOrNot(id, false));
    }

    @UserLog("撤销公告")
    @RequestMapping(value = "/inform/{id}/cancel", method = POST)
    public Object cancel(@PathVariable int id,
                         HttpServletRequest request) {
        return responseWrap(r -> {
            Staff user = tokenService.map2User(request);
            informService.cancel(id, user.getId());
        });
    }

    @UserLog("使公告过期")
    @RequestMapping(value = "/inform/{id}/outdate", method = POST)
    public Object outdate(@PathVariable int id,
                          HttpServletRequest request) {
        return responseWrap(r -> {
            Staff user = tokenService.map2User(request);
            informService.outdate(id, user.getId());
        });
    }
}