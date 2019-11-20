package com.bird.controller.buz;

import com.bird.common.PageInfo;
import com.bird.controller.BaseController;
import com.bird.service.buz.ILogService;
import com.bird.utils.DateUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author zhyyy
 **/
@RestController
public class LogController extends BaseController {

    @Autowired
    private ILogService logService;

    @RequestMapping(value = "/logs", method = GET)
    public Object list(@RequestParam(value = "ua", required = false) String userAccount,
                       @RequestParam(value = "uri", required = false) String uri,
                       @RequestParam(value = "des", required = false) String des,
                       @RequestParam(value = "rs", required = false) Long requestStart,
                       @RequestParam(value = "re", required = false) Long requestEnd,
                       @RequestParam(value = "is", required = false) Boolean isSuccess,
                       @RequestParam(value = "ht_min", required = false) Integer handlingTimeMin,
                       @RequestParam(value = "ht_max", required = false) Integer handlingTimeMax,
                       @RequestParam(value = "page", defaultValue = "1") int pageNum,
                       @RequestParam(value = "size", defaultValue = "20") int pageSize) {
        return responseWrap(() -> {
            Date requestStartDate = DateUtil.startOfThisDay(requestStart);
            Date requestEndDate = DateUtil.startOfNextDay(requestEnd);
            PageHelper.startPage(pageNum, pageSize);
            return new PageInfo<>(logService.list(userAccount, uri, des, requestStartDate, requestEndDate, isSuccess, handlingTimeMin, handlingTimeMax));
        });
    }
}
