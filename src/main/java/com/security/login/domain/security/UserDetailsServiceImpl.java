package com.security.login.domain.security;

import com.security.login.domain.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Login 사이트에 접근하면 가장 먼저 실행되는 메서드입니다.
     * 주어진 사용자 이름(username)을 기반으로 사용자 정보를 조회하고,
     * 조회된 정보를 UserDetails 객체로 변환하여 반환하면 Spring Security의 로그인 처리가 시작됩니다.
     * 이 메서드는 데이터베이스에서 사용자 정보를 조회하는 방식이나 MyBatis, JPA 등에 따라 다를 수 있습니다.
     *
     * @param username 사용자 이름(username)으로 사용자 정보를 조회하기 위한 식별자
     * @return UserDetails 객체로 변환된 사용자 정보
     * @throws UsernameNotFoundException 주어진 사용자 이름으로 사용자를 찾을 수 없는 경우 발생하는 예외
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserDetailsImpl(userRepository.findByName(username).orElseGet(null));
    }
}