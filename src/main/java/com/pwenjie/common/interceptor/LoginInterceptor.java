//TODO登录拦截器

package com.pwenjie.common.interceptor;

import com.pwenjie.common.constant.CacheConstants;
import com.pwenjie.common.constant.UserConstants;
import com.pwenjie.common.enums.ResponseCodeEnum;
import com.pwenjie.common.exception.BusinessException;
import com.pwenjie.common.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        log.debug("请求进入: {} {} - {}", method, requestURI, request.getRemoteAddr());

        // 检查是否是公开接口
        if (isPublicEndpoint(requestURI)) {
            return true;
        }

        // 获取Token
        String token = getTokenFromRequest(request);
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ResponseCodeEnum.UNAUTHORIZED, "请先登录");
        }

        // 验证Token有效性
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(ResponseCodeEnum.UNAUTHORIZED, "Token无效或已过期");
        }

        // 从Redis中验证Token
        String redisKey = CacheConstants.USER_TOKEN_PREFIX + token;
        Long userId = (Long) redisTemplate.opsForValue().get(redisKey);
        if (userId == null) {
            throw new BusinessException(ResponseCodeEnum.UNAUTHORIZED, "登录已过期，请重新登录");
        }

        // 刷新Token过期时间
        redisTemplate.expire(redisKey, CacheConstants.USER_TOKEN_EXPIRE, TimeUnit.SECONDS);

        // 从Token中获取用户信息
        String username = jwtUtil.getUsernameFromToken(token);
        Integer role = jwtUtil.getRoleFromToken(token);

        // 将用户信息存入请求属性
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        request.setAttribute("role", role);
        request.setAttribute("token", token);

        log.debug("用户认证通过: userId={}, username={}, role={}", userId, username, role);

        return true;
    }

    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 1. 从Header获取
        String token = request.getHeader(UserConstants.TOKEN_HEADER);
        if (StringUtils.hasText(token) && token.startsWith(UserConstants.TOKEN_PREFIX)) {
            return token.substring(UserConstants.TOKEN_PREFIX.length());
        }

        // 2. 从参数获取
        token = request.getParameter("token");
        if (StringUtils.hasText(token)) {
            return token;
        }

        return null;
    }

    /**
     * 判断是否是公开接口
     */
    private boolean isPublicEndpoint(String requestURI) {
        // 公开接口列表
        String[] publicEndpoints = {
                "/api/test/",           // 测试接口
                "/api/users/login",     // 用户登录
                "/api/users/register",  // 用户注册
                "/api/doc.html",        // 文档页面
                "/api/swagger",         // Swagger
                "/api/webjars",         // 静态资源
                "/api/v2/api-docs",     // API文档
                "/api/v3/api-docs",     // API文档v3
                "/api/favicon.ico",     // 图标
                "/api/css/",            // 前端CSS
                "/api/js/",             // 前端JS
                "/api/index.html",      // 前端页面
                "/api/error"            // 错误页面
        };

        for (String endpoint : publicEndpoints) {
            if (requestURI.startsWith(endpoint)) {
                return true;
            }
        }

        return false;
    }
}
