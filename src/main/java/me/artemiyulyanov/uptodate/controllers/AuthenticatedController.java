package me.artemiyulyanov.uptodate.controllers;

import lombok.extern.slf4j.Slf4j;
import me.artemiyulyanov.uptodate.models.User;
import me.artemiyulyanov.uptodate.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@Slf4j
public abstract class AuthenticatedController {
    @Autowired
    private UserService userService;

    protected Optional<User> getAuthorizedUser() {
        try {
            return userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}