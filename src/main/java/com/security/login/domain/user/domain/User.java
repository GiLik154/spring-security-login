package com.security.login.domain.user.domain;

import com.security.login.enums.UserGrade;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class User {
    /** User의 고유 키 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** User의 이름 */
    private String name;

    /** User의 비밀번호 */
    private String password;

    /** User의 등급 */
    @Enumerated(EnumType.STRING)
    private UserGrade userGrade;

    protected User() {
    }

    public User(String name, String password, UserGrade userGrade) {
        this.name = name;
        this.password = password;
        this.userGrade = userGrade;
    }
}
