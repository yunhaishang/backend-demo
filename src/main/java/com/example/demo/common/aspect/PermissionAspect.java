package com.example.demo.common.aspect;

import com.example.demo.common.annotation.RequiresPermissions;
import com.example.demo.common.context.UserContext;
import com.example.demo.common.exception.BusinessException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class PermissionAspect {
    @Around("@annotation(permissions)")
    public Object check(ProceedingJoinPoint joinPoint, RequiresPermissions permissions) throws Throwable {
        String requiredPerm = permissions.value();
        // 从 UserContext (ThreadLocal) 中获取当前用户的权限列表
        List<String> userPerms = UserContext.getPerms();

        if (!userPerms.contains(requiredPerm)) {
            throw new BusinessException(403, "你没有权限进行此操作");
        }
        return joinPoint.proceed();
    }
}
