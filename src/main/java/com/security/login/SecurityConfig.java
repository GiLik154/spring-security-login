package com.security.login;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * Spring Security에 대한 기본적인 설정을 정의하는 메서드입니다.
     * 이 메서드는 SecurityFilterChain을 생성하여 반환하는 Bean으로 등록됩니다.
     *
     * @param http HttpSecurity 객체, Spring Security 설정을 구성하기 위한 메서드가 제공됩니다.
     * @return SecurityFilterChain 객체, Spring Security 필터 체인을 나타냅니다.
     * @throws Exception HttpSecurity 구성 중에 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                    .csrf()
                .and()
                    .authorizeRequests()
                    .antMatchers("/admin").hasRole("ADMIN")
                    .antMatchers("/**").permitAll()
                .and()
                    .formLogin()
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/")
                .and()
                    .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login")
                .and()
                    .build();

        // `csrf()`: CSRF(Cross-Site Request Forgery)에 대한 설정을 활성화합니다.
        // `authorizeRequests()`: 요청에 대한 접근 권한 설정을 시작합니다.
        // `antMatchers("/admin").hasRole("ADMIN")`: '/admin' 경로에 접근하기 위해 'ADMIN' 권한이 필요합니다.
        // `antMatchers("/**").permitAll()`: 모든 경로에 대한 접근을 허용합니다.
        // `formLogin()`: 폼 로그인에 관한 설정을 시작합니다.
        // `loginProcessingUrl("/login")`: 로그인 처리를 담당하는 URL을 설정합니다.
        // `defaultSuccessUrl("/")`: 로그인 성공 후 사용자를 리디렉션할 기본 URL을 설정합니다.
        // `logout()`: 로그아웃에 관한 설정을 시작합니다.
        // `logoutUrl("/logout")`: 로그아웃 처리를 담당하는 URL을 설정합니다.
        // `logoutSuccessUrl("/login")`: 로그아웃 성공 후 사용자를 리디렉션할 URL을 설정합니다.
        // `build()`: SecurityFilterChain을 빌드하여 반환합니다.
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}