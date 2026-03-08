package com.example.demo.common.context;

import java.util.List;

public class UserContext {
    private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<List<String>> PERMS_HOLDER = new ThreadLocal<>();

    public static void setContext(Long id, List<String> perms) {
        USER_ID_HOLDER.set(id);
        PERMS_HOLDER.set(perms);
    }

    // 存入当前用户 ID
    public static void setUserId(Long userId) {
        USER_ID_HOLDER.set(userId);
    }

    // 获取当前用户 ID
    public static Long getUserId() {
        return USER_ID_HOLDER.get();
    }

    // 存入当前用户权限
    public static void setPerms(List<String> perms) {
        PERMS_HOLDER.set(perms);
    }

    // 获得当前用户权限
    public static List<String> getPerms() {
        return PERMS_HOLDER.get();
    }

    // 必须清理！防止内存泄漏
    public static void remove() {
        USER_ID_HOLDER.remove();
        PERMS_HOLDER.remove();
    }
}