package com.softserve.academy.antifraudsystem6802.controller;

import com.softserve.academy.antifraudsystem6802.model.User;
import com.softserve.academy.antifraudsystem6802.model.request.RoleRequest;
import com.softserve.academy.antifraudsystem6802.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.softserve.academy.antifraudsystem6802.model.Role.MERCHANT;
import static com.softserve.academy.antifraudsystem6802.model.Role.SUPPORT;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class UserController {
    UserService userService;

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    User createUser(@Valid @RequestBody User user) {
        return userService.register(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT));
    }

    @GetMapping("/list")
    List<User> listUsers() {
        return userService.listUsers();
    }

    @DeleteMapping("/user/{username}")
    Map<String, String> delete(@PathVariable String username) {
        if (userService.delete(username)) {
            return Map.of(
                    "username", username,
                    "status", "Deleted successfully!"
            );
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/role")
    User role(@RequestBody RoleRequest request) {
        var role = request.getRole();
        if (!Objects.equals(role, SUPPORT) && !Objects.equals(role, MERCHANT)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return userService.changeRole(request)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
