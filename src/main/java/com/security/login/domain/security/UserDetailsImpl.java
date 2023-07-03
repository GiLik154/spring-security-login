package com.security.login.domain.security;

import com.security.login.domain.user.domain.User;
import com.security.login.enums.UserGrade;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;


/**
 * 사용자 정보를 담은 User 객체를 UserDetails 인터페이스를 구현하여 래핑한 클래스입니다.
 * 주어진 User 객체에서 필요한 정보를 추출하여 UserDetails의 메서드를 구현합니다.
 */
public class UserDetailsImpl implements UserDetails {
    private final transient User user;

    /**
     * 사용자의 등급을 반환하는 메서드입니다.
     *
     * @return 사용자의 등급
     */
    public UserGrade getUserGrade(){
        return user.getUserGrade();
    }

    /**
     * UserDetail 객체를 생성하는 생성자입니다.
     *
     * @param user 사용자 정보를 담은 User 객체
     */
    public UserDetailsImpl(User user) {
        this.user = user;
    }

    /**
     * 사용자의 권한 정보를 반환하는 메서드입니다.
     * User 객체에서 사용자 등급(UserGrade)을 추출하여 해당 등급의 권한을 반환합니다.
     *
     * @return 사용자의 권한 정보를 나타내는 GrantedAuthority 컬렉션
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add((GrantedAuthority) () -> user.getUserGrade().getAuthority());
        return collection;
    }

    /**
     * 사용자의 비밀번호를 반환하는 메서드입니다.
     *
     * @return 사용자의 비밀번호
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 사용자의 이름(username)을 반환하는 메서드입니다.
     *
     * @return 사용자의 이름(username)
     */
    @Override
    public String getUsername() {
        return user.getName();
    }

    /**
     * 사용자 계정의 만료 여부를 반환하는 메서드입니다.
     * 항상 true를 반환하여 계정이 만료되지 않았음을 나타냅니다.
     *
     * @return 사용자 계정의 만료 여부
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    /**
     * 사용자 계정의 잠김 상태 여부를 반환하는 메서드입니다.
     * 항상 true를 반환하여 계정이 잠겨있지 않음을 나타냅니다.
     *
     * @return 사용자 계정의 잠김 상태 여부
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


    /**
     * 사용자 계정의 비밀번호 만료 여부를 반환하는 메서드입니다.
     * 항상 true를 반환하여 비밀번호가 만료되지 않았음을 나타냅니다.
     *
     * @return 사용자 계정의 비밀번호 만료 여부
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    /**
     * 사용자 계정의 활성화 여부를 반환하는 메서드입니다.
     * 항상 true를 반환하여 계정이 활성화되어 있음을 나타냅니다.
     *
     * @return 사용자 계정의 활성화 여부
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}