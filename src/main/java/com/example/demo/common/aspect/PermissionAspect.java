package com.example.demo.common.aspect;

import com.example.demo.common.annotation.RequiresRoles;
import com.example.demo.common.context.UserContext;
import com.example.demo.common.exception.BusinessException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionAspect {
    @Around("@annotation(roles)")
    public Object check(ProceedingJoinPoint joinPoint, RequiresRoles roles) throws Throwable {
        String[] requireRoles = roles.value();
        // 从 UserContext (ThreadLocal) 中获取当前用户的角色
        String userRole = UserContext.getRole();

        for (String role : requireRoles) {
            if (userRole.equals(role)) {
                return joinPoint.proceed();
            }
        }

        throw new BusinessException(403, "你没有权限进行此操作");
    }
}
