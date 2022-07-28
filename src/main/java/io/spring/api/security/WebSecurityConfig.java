package io.spring.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static java.util.Arrays.asList;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 开启跨域时，使用过滤器，这种方式先于拦截器执行
     * @return
     */
    @Bean
    public JwtTokenFilter jwtTokenFilter(){
        return new JwtTokenFilter();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    //授权
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .cors()
                .and()//禁止csrf，开启跨域
                .exceptionHandling()//异常的处理
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS)
                .permitAll()//options的http方法都允许
                .antMatchers("/graphiql")
                .permitAll()
                .antMatchers("/graphql")
                .permitAll()
                .anyRequest()
                .authenticated();

        //自定义过滤器认证用户名密码
        http.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
    //跨域设置
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        final CorsConfiguration configuration=new CorsConfiguration();

        //开放哪些ip、端口、域名的访问权限，星号表示开放所有域
        configuration.setAllowedOrigins(asList("*"));
        //开放哪些Http方法，允许跨域访问,没有options
        configuration.setAllowedMethods(asList("HEAD","GET","POST","PUT","DELETE","PATCH"));
        //是否允许发送Cookie信息  ？？？？？？？？？？设为true还是false
        // setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the
        // wildcard '*' when the request's credentials mode is 'include'.
        configuration.setAllowCredentials(true);
        //允许HTTP请求中的携带哪些Header信息
        configuration.setAllowedHeaders(asList("Authorization","Cache-Control","Content-Type"));
        //添加映射路径，“/**”表示对所有的路径实行全局跨域访问权限的设置
       final UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
       source.registerCorsConfiguration("/**",configuration);
       return source;
    }
}
