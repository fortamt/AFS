package com.softserve.academy.antifraudsystem6802.controller;

import com.softserve.academy.antifraudsystem6802.model.entity.User;
import com.softserve.academy.antifraudsystem6802.model.request.RequestLock;
import com.softserve.academy.antifraudsystem6802.model.request.RoleRequest;
import com.softserve.academy.antifraudsystem6802.model.response.ResponseDelete;
import com.softserve.academy.antifraudsystem6802.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

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
    @ResponseStatus(HttpStatus.OK)
    List<User> listUsers() {
        return userService.listUsers();
    }

    @DeleteMapping("/user/{username}")
    ResponseDelete delete(@PathVariable String username) {
        if (userService.delete(username)) {
            ResponseDelete responseDelete = new ResponseDelete();
            responseDelete.setUsername(username);
            responseDelete.setStatus("Deleted successfully!");
            return responseDelete;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/role")
    @ResponseStatus(HttpStatus.OK)
    User role(@RequestBody RoleRequest request) {
        return userService.changeRole(request)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/access")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> doLock(@RequestBody RequestLock lockUsers) {
        return userService.lock(lockUsers);
    }

}
