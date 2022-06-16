package com.softserve.academy.antifraudsystem6802.service;

import com.softserve.academy.antifraudsystem6802.model.Role;
import com.softserve.academy.antifraudsystem6802.model.entity.User;
import com.softserve.academy.antifraudsystem6802.model.request.RequestLock;
import com.softserve.academy.antifraudsystem6802.model.request.RoleRequest;
import com.softserve.academy.antifraudsystem6802.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.softserve.academy.antifraudsystem6802.model.Role.*;

@Service
public class UserService implements UserDetailsService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameIgnoreCase(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found: " + username));
    }

    @Transactional
    public Optional<User> register(User user) {
        Optional<User> existedUser = userRepository.findByUsernameIgnoreCase(user.getUsername());
        if (existedUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        if(user.getRole() != null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (userRepository.count() == 0) {
            user.setRole(ADMINISTRATOR);
            user.setAccountNonLocked(true);
        } else {
            user.setRole(MERCHANT);
            user.setAccountNonLocked(false);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return Optional.of(userRepository.save(user));
    }

    public List<User> listUsers() {
        return userRepository.findAll(
                Sort.sort(User.class).by(User::getId).ascending()
        );
    }

    public Map<String, String> lock(RequestLock requestLock) {
        String operation = requestLock.getOperation();

        User user = userRepository.findByUsernameIgnoreCase(requestLock.getUsername()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        if (user.getRole().name().equals(ADMINISTRATOR.name())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (!user.getRole().name().equals(ADMINISTRATOR.name()) && operation.equals("LOCK")) {
            user.setAccountNonLocked(false);
            userRepository.save(user);
            return Map.of("status", "User " + user.getUsername() + " locked!");
        } else if (!user.getRole().name().equals(ADMINISTRATOR.name()) && operation.equals("UNLOCK")) {
            user.setAccountNonLocked(true);
            userRepository.save(user);
            return Map.of("status", "User " + user.getUsername() + " unlocked!");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public boolean delete(String username) {
        return userRepository.deleteByUsernameIgnoreCase(username) == 1;
    }

    @Transactional
    public Optional<User> changeRole(RoleRequest request) {
        Optional<User> optionalUser = userRepository.findByUsernameIgnoreCase(request.getUsername());
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        var user = optionalUser.get();
        var role = request.getRole();
        validateRole(user, role);
        user.setRole(role);
        return Optional.of(userRepository.save(user));
    }

    private void validateRole(User user, Role role) {
        if (!Objects.equals(role, SUPPORT) && !Objects.equals(role, MERCHANT)
                || Objects.equals(role, ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(user.getRole(), role)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }
}
