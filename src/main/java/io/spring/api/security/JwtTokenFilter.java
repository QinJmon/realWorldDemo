package io.spring.api.security;

import io.spring.core.service.JwtService;
import io.spring.core.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

/**
 * 自定义过滤器认证用户名密码
 */
//对@Autowired代码检查报错。
@SuppressWarnings("SpringJavaAutowiringInspection")
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;


    private final String header="Authorization";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //从请求中获取请求头
        getTokenString(request.getHeader(header))
                .flatMap(token -> jwtService.getSubFromToken(token))
                //ifPresent 用于对过滤出的数据如果存在。有数据的话就可以进行指定的操作。如果存在token进一步验证
                .ifPresent(
                        id -> {
                            //如果用户的身份为null，去数据库中查找该用户，如果存在创建用户身份？？？？？
                            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                                userRepository.findById(id).ifPresent(
                                        user -> {
                                            UsernamePasswordAuthenticationToken authenticationToken =
                                                    new UsernamePasswordAuthenticationToken(
                                                            user, null, Collections.emptyList());
                                            authenticationToken.setDetails(
                                                    new WebAuthenticationDetailsSource().buildDetails(request));
                                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                                        });
                            }
                        });

        //过滤
        filterChain.doFilter(request,response);
    }

    private Optional<String> getTokenString(String header) {
        if(header==null){
            return Optional.empty();
        }else{
            String[] split = header.split(" ");
            if(split.length<2){
                return Optional.empty();
            }else{
                return Optional.ofNullable(split[1]);
            }
        }
    }
}
