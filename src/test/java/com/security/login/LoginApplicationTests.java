package com.security.login;

import com.security.login.domain.user.domain.User;
import com.security.login.domain.user.domain.UserRepository;
import com.security.login.enums.UserGrade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class LoginApplicationTests {
    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    LoginApplicationTests(UserRepository userRepository, PasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Test
    void contextLoads() {
        User user = new User("234", bCryptPasswordEncoder.encode("234"), UserGrade.ADMIN);
        userRepository.save(user);
    }

}
