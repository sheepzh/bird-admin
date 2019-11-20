package com.bird.aspect;

import com.alibaba.fastjson.JSON;
import com.bird.common.Response;
import com.bird.controller.UserLog;
import com.bird.dao.StaffActionLogMapper;
import com.bird.model.entity.Staff;
import com.bird.model.entity.StaffActionLog;
import com.bird.service.system.ITokenService;
import com.bird.utils.DateUtil;
import com.bird.utils.IoUtil;
import com.bird.utils.WebUtil;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.bird.utils.StringUtil.isBlank;

/**
 * 用户操作日志处理
 *
 * @author zhyyy
 **/
@Aspect
@Component
public class WebLogAspect {
    private final Logger LOG = Logger.getLogger(getClass());
    @Autowired
    private StaffActionLogMapper logMapper;
    @Autowired
    private ITokenService tokenService;

    private StaffActionLog temp;

    @Pointcut(value = "execution(public * com.bird.controller..*.*(..))&&@annotation(com.bird.controller.UserLog)")
    public void logAspect() {
    }

    @Before("logAspect()")
    public void handleBefore(JoinPoint joinPoint) throws Throwable {
        // 获取请求分配方法
        Signature signature = joinPoint.getSignature();
        UserLog userLog = needAspect(signature);
        if (userLog == null) {
            return;
        }
        // 开始记录操作日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        temp = new StaffActionLog();
        Date requestTime = new Date();
        // 获取用户信息
        if (!userLog.noToken()) {
            Staff staff;
            if ((staff = tokenService.map2User(request)) == null) {
                LOG.error("用户token丢失，日志记录失败");
                return;
            }
            temp.setStaffId(staff.getId());
            temp.setStaffAccount(staff.getAccount());
        }
        // 填写请求信息
        // URI地址
        String uri = request.getRequestURI();
        // 参数
        Map<String, String[]> paramMap = request.getParameterMap();
        String paramStr = paramMap.entrySet()
                .stream()
                // 排除token
                .filter(e -> !"token".equals(e.getKey()))
                .map(e -> {
                    String[] val = e.getValue();
                    String param = e.getKey() + '=';
                    switch (val.length) {
                        case 0:
                            break;
                        case 1:
                            param += val[0];
                            break;
                        default:
                            param += Arrays.toString(val);
                    }
                    return param;
                })
                .collect(Collectors.joining("&"));
        if (!isBlank(paramStr)) {
            uri = uri + "?" + paramStr;
        }
        temp.setDes(userLog.value());
        temp.setRequestUri(uri);
        temp.setControllerMethod(signature.getDeclaringTypeName() + '.' + signature.getName());
        temp.setRequestMethod(request.getMethod());
        temp.setRequestSourceIp(WebUtil.getIpAddress(request));
        String requestBody = IoUtil.stream2String(request.getInputStream());
        temp.setRequestBody(requestBody.length() > 200 ? "请求体过长" : requestBody);
        temp.setRequestTime(requestTime);

        LOG.info(String.format("REQUEST: %s at %s %s from %s ",
                temp.getRequestMethod(),
                DateUtil.formatDate(temp.getRequestTime()),
                temp.getRequestUri(),
                temp.getRequestSourceIp()));
    }

    @AfterReturning(returning = "ret", pointcut = "logAspect()")
    public void handleAfter(Object ret) {
        if (temp == null) {
            // 未进行UserLog标记的请求
            return;
        }
        // 处理完请求，返回内容
        if (ret instanceof Map) {
            Map<String, Object> response = (Map<String, Object>) ret;
            Date responseTime = new Date();
            if (Objects.equals(response.get(Response.RESULT_CODE), Response.RESPONSE_SUCCESS)) {
                temp.setSuccess(true);
            } else {
                temp.setSuccess(false);
                temp.setErrorMessage((String) response.get(Response.RESULT_MSG));
            }
            String responseMap = JSON.toJSONString(response);
            temp.setHandlingTime((int) (responseTime.getTime() - temp.getRequestTime().getTime()));
            temp.setResponseTime(responseTime);
            temp.setResponse(responseMap.length() > 400 ? "返回体过长" : responseMap);
            // 登录接口特殊处理，填补用户信息
            String url = temp.getRequestUri();
            if (url != null && url.startsWith("/user/login")) {
                Staff user = (Staff) response.get("user");
                if (user != null) {
                    temp.setStaffId(user.getId());
                    temp.setStaffAccount(user.getAccount());
                }
            }
            if (!"root".equals(temp.getStaffAccount())) {
                logMapper.insertSelective(temp);
            }
            LOG.info(String.format("RESPONSE at %s to %s: %s",
                    DateUtil.formatDate(responseTime),
                    DateUtil.formatDate(temp.getRequestTime()),
                    responseMap));
        }
    }

    private UserLog needAspect(Signature signature) {
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        // 获取方法上的UserLog注解
        UserLog userLog = method.getAnnotation(UserLog.class);
        return userLog == null || !userLog.need() ? null : userLog;
    }
}
