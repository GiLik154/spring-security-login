package com.security.login.controller;

import com.security.login.domain.security.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    @GetMapping("")
    public String session(HttpSession session, Model model) {

        SecurityContextHolder.getContext().getAuthentication().getDetails();

        SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        Authentication authentication = securityContext.getAuthentication();
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();

        model.addAttribute("userName", userDetailsImpl.getUsername());

        return "thymeleaf/index";
    }

    @GetMapping("")
    public String securityContextHolder(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) principal;

        model.addAttribute("userName", userDetailsImpl.getUsername());

        return "thymeleaf/holder";
    }

    @GetMapping("/admin")
    public String authentication(Model model, Authentication authentication) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) principal;

        model.addAttribute("userName", userDetailsImpl.getUsername());

        return "thymeleaf/admin";
    }
}