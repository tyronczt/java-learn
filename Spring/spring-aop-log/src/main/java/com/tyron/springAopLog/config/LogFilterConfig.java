package com.tyron.springAopLog.config;

import com.alibaba.fastjson.JSON;
import com.tyron.springAopLog.annotation.Log;
import com.tyron.springAopLog.emun.BusinessStatus;
import com.tyron.springAopLog.model.SysOperLog;
import com.tyron.springAopLog.util.IpUtils;
import com.tyron.springAopLog.util.ServletUtils;
import com.tyron.springAopLog.util.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @Description: 日志过滤配置类
 * @Author: tyron
 * @Date: Created in 2021/7/24
 */
@Aspect
@Component
public class LogFilterConfig {
    private static final Logger logger = LoggerFactory.getLogger(LogFilterConfig.class);

    private ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<Map<String, Object>>();

    // 配置织入点
    @Pointcut("@annotation(com.tyron.springAopLog.annotation.Log)")
    public void logPointCut() {
    }

    /**
     * 接收请求，并记录数据
     *
     * @param joinPoint
     * @param log
     */
    @Before(value = "logPointCut()&& @annotation(log)")
    public void doBefore(JoinPoint joinPoint, Log log) {
        // 接收到请求
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        // 记录请求内容，threadInfo存储所有内容
        Map<String, Object> threadInfo = new HashMap<>();
        logger.info("URL : " + request.getRequestURL());
        threadInfo.put("url", request.getRequestURL());
        logger.info("URI : " + request.getRequestURI());
        threadInfo.put("uri", request.getRequestURI());
        logger.info("HTTP_METHOD : " + request.getMethod());
        threadInfo.put("httpMethod", request.getMethod());
        logger.info("REMOTE_ADDR : " + request.getRemoteAddr());
        threadInfo.put("ip", request.getRemoteAddr());
        logger.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "."
                + joinPoint.getSignature().getName());
        threadInfo.put("classMethod",
                joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        logger.info("ARGS : " + Arrays.toString(joinPoint.getArgs()));
        threadInfo.put("args", Arrays.toString(joinPoint.getArgs()));
        logger.info("USER_AGENT" + request.getHeader("User-Agent"));
        threadInfo.put("userAgent", request.getHeader("User-Agent"));
        logger.info("执行方法：" + log.title());
        threadInfo.put("methodName", log.title());
        threadLocal.set(threadInfo);
    }

    /**
     * 获取执行时间
     *
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
//    @Around(value = "logPointCut()")
//    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        long startTime = System.currentTimeMillis();
//        Object ob = proceedingJoinPoint.proceed();
//        Map<String, Object> threadInfo = threadLocal.get();
//        Long takeTime = System.currentTimeMillis() - startTime;
//        threadInfo.put("takeTime", takeTime);
//        logger.info("耗时：" + takeTime + " 毫秒");
//        threadLocal.set(threadInfo);
//        return ob;
//    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "logPointCut()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        handleLog(joinPoint, null, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * 无异常时这个AfterThrowing方法不会执行。
     * 有异常时，如果Around存在则无法执行这个AfterThrowing方法，如果Around不存在才会执行这个AfterThrowing方法
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, final Exception e, Object jsonResult) {
        try {
            // 获得注解
            Log controllerLog = getAnnotationLog(joinPoint);
            if (controllerLog == null) {
                return;
            }

            // 获取当前的用户
//            LoginUser loginUser = SpringUtils.getBean(TokenService.class).getLoginUser(ServletUtils.getRequest());

            // *========数据库日志=========*//
            SysOperLog operLog = new SysOperLog();
            operLog.setStatus(BusinessStatus.SUCCESS.ordinal());
            // 请求的地址
            String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
            operLog.setOperIp(ip);
            // 返回参数
            operLog.setJsonResult(JSON.toJSONString(jsonResult));

            operLog.setOperUrl(ServletUtils.getRequest().getRequestURI());
//            if (loginUser != null) {
//                operLog.setOperName(loginUser.getUsername());
//            }
            if (e != null) {
                operLog.setStatus(BusinessStatus.FAIL.ordinal());
                operLog.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
            }
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");
            // 设置请求方式
            operLog.setRequestMethod(ServletUtils.getRequest().getMethod());
            // 处理设置注解上的参数
            getControllerMethodDescription(joinPoint, controllerLog, operLog);
            // 保存数据库
        } catch (Exception exp) {
            // 记录本地异常日志
            logger.error("==前置通知异常==");
            logger.error("异常信息:{}", exp.getMessage());
            exp.printStackTrace();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param log     日志
     * @param operLog 操作日志
     * @throws Exception
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, Log log, SysOperLog operLog) throws Exception {
        // 设置action动作
        operLog.setBusinessType(log.businessType().ordinal());
        // 设置标题
        operLog.setTitle(log.title());
        // 设置操作人类别
        operLog.setOperatorType(log.operatorType().ordinal());
        // 是否需要保存request，参数和值
        if (log.isSaveRequestData()) {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(joinPoint, operLog);
        }
    }

    /**
     * 获取请求的参数，放到log中
     *
     * @param operLog 操作日志
     * @throws Exception 异常
     */
    private void setRequestValue(JoinPoint joinPoint, SysOperLog operLog) throws Exception {
        String requestMethod = operLog.getRequestMethod();
        if (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod)) {
            String params = argsArrayToString(joinPoint.getArgs());
            // 参数截取
            params = StringUtils.substring(params, 0, 2000);
            logger.info("PARAMS : " + params);
            operLog.setOperParam(params);
        } else {
            Map<?, ?> paramsMap = (Map<?, ?>) ServletUtils.getRequest().getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            operLog.setOperParam(StringUtils.substring(paramsMap.toString(), 0, 2000));
        }
    }

    /**
     * 是否存在注解，如果存在就获取
     */
    private Log getAnnotationLog(JoinPoint joinPoint) throws Exception {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if (method != null) {
            return method.getAnnotation(Log.class);
        }
        return null;
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray) {
        String params = "";
        if (paramsArray != null && paramsArray.length > 0) {
            for (int i = 0; i < paramsArray.length; i++) {
                if (StringUtils.isNotNull(paramsArray[i]) && !isFilterObject(paramsArray[i])) {
                    Object jsonObj = JSON.toJSON(paramsArray[i]);
                    params += jsonObj.toString() + " ";
                }
            }
        }
        return params.trim();
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Iterator iter = collection.iterator(); iter.hasNext(); ) {
                return iter.next() instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Iterator iter = map.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iter.next();
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }
}
