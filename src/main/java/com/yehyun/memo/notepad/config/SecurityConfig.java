package com.yehyun.memo.notepad.config;

import com.yehyun.memo.notepad.security.handler.CustomSuccessHandler;
import com.yehyun.memo.notepad.security.jwt.JwtFilter;
import com.yehyun.memo.notepad.security.jwt.JwtLoginSuccessProcessor;
import com.yehyun.memo.notepad.security.jwt.JwtRequestParser;
import com.yehyun.memo.notepad.security.jwt.JwtUtil;
import com.yehyun.memo.notepad.security.service.CustomMemberDetailsService;
import com.yehyun.memo.notepad.security.service.CustomOAuth2MemberService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final JwtRequestParser jwtRequestParser;
    private final JwtLoginSuccessProcessor jwtLoginSuccessProcessor;

    private final CustomSuccessHandler customSuccessHandler;

    private final CustomMemberDetailsService customMemberDetailsService;
    private final CustomOAuth2MemberService customOAuth2MemberService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customMemberDetailsService);
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf((auth) -> auth.disable());

        http
                .httpBasic((auth) -> auth.disable());

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/oauth2/**", "/image/**", "/css/**", "/login", "/login/signup", "/login/no").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        http
                .formLogin((auth) -> auth
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("loginId")
                        .successHandler(customSuccessHandler)
                        .failureUrl("/login?error=fail")
                        .permitAll()
                );

        http
                .oauth2Login((oauth2) -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint((userInfo ) -> userInfo.userService(customOAuth2MemberService))
                        .successHandler(customSuccessHandler)
                        .failureUrl("/login?error=fail")
                );

        http
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            Cookie cookie = new Cookie("Authorization", null);
                            cookie.setMaxAge(0);
                            cookie.setPath("/");
                            response.addCookie(cookie);

                            response.sendRedirect("/");
                        })
                );

        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .addFilterBefore(new JwtFilter(jwtUtil, jwtRequestParser, jwtLoginSuccessProcessor), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
