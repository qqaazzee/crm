package com.xxxx.crm.aspect;

import com.xxxx.crm.annoation.RequiredPermission;
import com.xxxx.crm.exceptions.AuthException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@Component
@Aspect
public class PermissionProxy {

    @Resource
    private HttpSession session;

    /**
     * 切面会拦截指定包下的指定注解
     * 拦截com.xxxx.crm.annoation的RequiredPermission的注解
     * @return
     */
    @Around(value = "@annotation(com.xxxx.crm.annoation.RequiredPermission)")
    public Object around(ProceedingJoinPoint pjp){
        Object result = null;
        //的带当前用户拥有的权限 (session作用域)
        List<String> permission = (List<String>) session.getAttribute("permission");
        //判断用户是否拥有权限
        if (null == permission || permission.size() < 1) {
            //抛出认证异常
            throw  new AuthException();
        }

        //得到对应的目标
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        //得到方法上的注解
        RequiredPermission requiredPermission = methodSignature.getMethod().getDeclaredAnnotation(RequiredPermission.class);
        //判断注解上对应的状态码
        if(!(permission.contains(requiredPermission.code()))) {
            throw  new AuthException();
        }

        return result;

    }

}
