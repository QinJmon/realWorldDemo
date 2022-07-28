package io.spring.graphql;

import io.spring.core.user.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * 用于获取当前用户的工具类
 */
public class SecurityUtil {

    public static Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal()==null || authentication instanceof AnonymousAuthenticationToken){
            return Optional.empty();
        }
        io.spring.core.user.User currentUser = (io.spring.core.user.User) authentication.getPrincipal();
       // 返回一个描述给定非空值的Optional。
        return Optional.of(currentUser);
    }
}
