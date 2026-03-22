package com.example.demo.interceptor;

import com.example.demo.common.exception.BusinessException;
import com.example.demo.common.context.UserContext;
import com.example.demo.utils.JwtUtils;
import com.example.demo.utils.RedisUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.apache.commons.lang3.StringUtils;

@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;
    private final RedisUtils redisUtils;

    public JwtInterceptor(JwtUtils jwtUtils, RedisUtils redisUtils) {
        this.jwtUtils = jwtUtils;
        this.redisUtils = redisUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. 获取请求头中的 Token
        String token = request.getHeader("Authorization");

        // 2. 判空：如果没传 Token，直接抛出未登录异常（会被 GlobalExceptionHandler 捕获）
        if (StringUtils.isBlank(token)) {
            log.warn("请求拒绝：未检测到 Authorization 头部");
            throw new BusinessException(401, "请先登录");
        }

        // 3. 去除 Bearer 前缀获取真实 JWT 字符串
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            // 4. 解析 JWT 并验证合法性、是否过期（载荷层面的过期）
            Claims claims = jwtUtils.extractClaims(token);

            // 5. 提取用户标识 ID (注意类型转换，JWT 默认可能解析为 Integer)
            Long userId = Long.valueOf(claims.get("id").toString());

            // 6. Redis 准入校验
            // 即使 JWT 没过期，如果 Redis 里没有这个用户的 Key，说明登录态已失效（如：手动退出或太久没操作）
            String redisKey = "login:token:" + userId;
            Object storedToken = redisUtils.get(redisKey);

            if (storedToken == null || !storedToken.equals(token)) {
                log.warn("请求拒绝：用户 {} 的 Redis 登录凭证已失效", userId);
                throw new BusinessException(401, "登录已失效，请重新登录");
            }

            // 7. 滑动续期 只要用户有操作，就将 Redis 中的过期时间重置（例如 30 分钟）
            // 这里不生成新 Token，只是给旧 Token 的“户口”延长寿命
            redisUtils.expire(redisKey, 60 * 30);

            // 8. 将 redis 的数据存到 UserContext
            UserContext.setUserId(userId);
            // redisKey = "auth:perms:" + userId;
            // List<String> perms = (List<String>) redisUtils.get(redisKey);
            // UserContext.setPerms(perms);

            log.info("用户 {} 校验通过，Token 已续期", userId);
            return true;

        } catch (Exception e) {
            log.error("令牌验证失败: {}", e.getMessage());
            throw new BusinessException(401, "令牌验证失败，请重新登录");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 9. 非常重要：请求结束后必须清理 ThreadLocal，防止内存泄漏和用户信息错乱
        UserContext.remove();
    }
}