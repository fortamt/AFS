package com.softserve.academy.antifraudsystem6802.service;

import com.softserve.academy.antifraudsystem6802.model.Role;
import com.softserve.academy.antifraudsystem6802.model.User;
import com.softserve.academy.antifraudsystem6802.model.request.RequestLock;
import com.softserve.academy.antifraudsystem6802.model.request.RoleRequest;
import com.softserve.academy.antifraudsystem6802.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder encoder;
    private UserService underTest;

    @BeforeEach
    void setUp() {
        underTest =new UserService(userRepository, encoder);
    }

    @Test
    void loadUserByUsername() {
        User user = new User(
                null,
                "Artem",
                "fortamt",
                "password",
                false,
                null);

        Mockito.when(userRepository.findByUsernameIgnoreCase(user.getUsername())).thenReturn(Optional.of(user));

        underTest.loadUserByUsername(user.getUsername());

        verify(userRepository).findByUsernameIgnoreCase(user.getUsername());
    }

    @Test
    void loadUserByUsernameNotExistedUsername() {
        assertThatThrownBy(() -> underTest.loadUserByUsername("UsernameNotExisted"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User UsernameNotExisted not found");
    }

    @Test
    void canRegisterFirstUserAsAdministrator() {
        //given
        User user = new User(
                null,
                "Artem",
                "fortamt",
                "password",
                false,
                null);

        Mockito.when(userRepository.save(user)).thenReturn(user);

        //when
        underTest.register(user);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser).isEqualTo(user);
        assertThat(capturedUser.getRole()).isEqualTo(Role.ADMINISTRATOR);
        assertThat(capturedUser.isAccountNonLocked()).isEqualTo(true);
    }

    @Test
    void canRegisterOtherNotFirstUserAsMerchant() {
        //given
        User user = new User(
                null,
                "Artem",
                "second",
                "password",
                false,
                null);

        given(userRepository.count()).willReturn(1L);
        Mockito.when(userRepository.save(user)).thenReturn(user);

        //when
        underTest.register(user);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser).isEqualTo(user);
        assertThat(capturedUser.getRole()).isEqualTo(Role.MERCHANT);
        assertThat(capturedUser.isAccountNonLocked()).isEqualTo(false);
    }

    @Test
    void cannotRegisterExistedUser() {
        //given
        User user = new User(
                null,
                "Artem",
                "second",
                "password",
                false,
                null);

        given(userRepository.existsByUsernameIgnoreCase(user.getUsername())).willReturn(true);

        //when
        Optional<User> tryToRegisterUser = underTest.register(user);
        assertThat(tryToRegisterUser).isEqualTo(Optional.empty());
    }

    @Test
    void cannotRegisterWithWrongParameters() {
        //given
        User user = new User(
                null,
                "Artem",
                "second",
                "password",
                false,
                Role.ADMINISTRATOR);  // wrong here (must be null)
        //when
        assertThatThrownBy(() -> underTest.register(user))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void canGetListUsers() {
        //when
        underTest.listUsers();
        //then
        verify(userRepository).findAll(Sort.sort(User.class).by(User::getId).ascending());
    }

    @Test
    void lockUnlock() {
        User user = new User(
                null,
                "Artem",
                "second",
                "password",
                false,
                Role.MERCHANT);  // wrong here (must be null)
        RequestLock requestLock = new RequestLock();
        requestLock.setUsername(user.getUsername());
        requestLock.setOperation("UNLOCK");

        given(userRepository.findByUsernameIgnoreCase(user.getUsername())).willReturn(Optional.of(user));

        underTest.lock(requestLock);
        verify(userRepository).save(user);
        assertThat(user.isAccountNonLocked()).isEqualTo(true);
    }

    @Test
    void lockNotExistedUser() {
        RequestLock requestLock = new RequestLock();
        requestLock.setUsername("NotExisted");
        requestLock.setOperation("UNLOCK");


        assertThatThrownBy(() -> underTest.lock(requestLock))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void lock() {
        User user = new User(
                null,
                "Artem",
                "second",
                "password",
                true,
                Role.MERCHANT);  // wrong here (must be null)
        RequestLock requestLock = new RequestLock();
        requestLock.setUsername(user.getUsername());
        requestLock.setOperation("LOCK");


        given(userRepository.findByUsernameIgnoreCase(user.getUsername())).willReturn(Optional.of(user));

        underTest.lock(requestLock);
        verify(userRepository).save(user);
        assertThat(user.isAccountNonLocked()).isEqualTo(false);
    }

    @Test
    void lockException() {
        User user = new User(
                null,
                "Artem",
                "second",
                "password",
                true,
                Role.MERCHANT);  // wrong here (must be null)
        RequestLock requestLock = new RequestLock();
        requestLock.setUsername(user.getUsername());
        requestLock.setOperation("WRONG");

        given(userRepository.findByUsernameIgnoreCase(user.getUsername())).willReturn(Optional.of(user));

        assertThatThrownBy(() -> underTest.lock(requestLock))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void lockException2() {
        User user = new User(
                null,
                "Artem",
                "second",
                "password",
                true,
                Role.ADMINISTRATOR);  // wrong here (must be null)
        RequestLock requestLock = new RequestLock();
        requestLock.setUsername(user.getUsername());
        requestLock.setOperation("LOCK");

        given(userRepository.findByUsernameIgnoreCase(user.getUsername())).willReturn(Optional.of(user));

        assertThatThrownBy(() -> underTest.lock(requestLock))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void delete() {
        User user = new User(
                null,
                "Artem",
                "fortamt",
                "password",
                false,
                null);

        Mockito.when(userRepository.deleteByUsernameIgnoreCase(user.getUsername())).thenReturn(1);

        boolean flag = underTest.delete(user.getUsername());
        verify(userRepository).deleteByUsernameIgnoreCase(user.getUsername());
        assertThat(flag).isEqualTo(true);
    }

    @Test
    void changeRole() {
        User user = new User(
                null,
                "Artem",
                "fortamt",
                "password",
                false,
                Role.MERCHANT);
        RoleRequest request = new RoleRequest();
        request.setUsername(user.getUsername());
        request.setRole(Role.SUPPORT);

        Mockito.when(userRepository.findByUsernameIgnoreCase(user.getUsername())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(user)).thenReturn(user);

        underTest.changeRole(request);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();

        assertThat(user.getRole()).isEqualTo(request.getRole());
        assertThat(capturedUser.getRole()).isEqualTo(user.getRole());
        assertThat(user.getRole()).isEqualTo(Role.SUPPORT);
    }

    @Test
    void changeRoleNotExistedUser() {

        RoleRequest request = new RoleRequest();
        request.setUsername("NotExisted");
        request.setRole(Role.SUPPORT);

        Mockito.when(userRepository.findByUsernameIgnoreCase(request.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.changeRole(request))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void changeRoleBadRequest() {
        User user = new User(
                null,
                "Artem",
                "fortamt",
                "password",
                false,
                Role.MERCHANT);
        RoleRequest request = new RoleRequest();
        request.setUsername(user.getUsername());
        request.setRole(Role.ADMINISTRATOR);

        Mockito.when(userRepository.findByUsernameIgnoreCase(request.getUsername())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> underTest.changeRole(request))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void changeRoleConflict() {
        User user = new User(
                null,
                "Artem",
                "fortamt",
                "password",
                false,
                Role.MERCHANT);
        RoleRequest request = new RoleRequest();
        request.setUsername(user.getUsername());
        request.setRole(Role.MERCHANT);

        Mockito.when(userRepository.findByUsernameIgnoreCase(request.getUsername())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> underTest.changeRole(request))
                .isInstanceOf(ResponseStatusException.class);
    }
}