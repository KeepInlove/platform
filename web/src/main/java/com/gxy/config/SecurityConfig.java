package com.gxy.config;

import com.gxy.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @Classname SecurityConfig
 * @Date 2024/10/29
 * @Created by guoxinyu
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 关闭 CSRF 保护
                .csrf(csrf -> csrf.disable())
                // 设置 Session 管理策略为无状态
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 对于登录接口允许匿名访问
                        .requestMatchers("/login").anonymous()
                        // 其他请求需要认证
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form // 使用函数式配置的 formLogin
                        .loginPage("/login") // 自定义登录页面
                        .permitAll() // 允许所有用户访问登录页面
                )
                .httpBasic(httpBasic -> httpBasic // 使用函数式配置的 httpBasic
                        .realmName("MyApp") // 自定义 Realm 名称
                );
        http.addFilter(jwtAuthenticationTokenFilter);

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
