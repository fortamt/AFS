package com.softserve.academy.antifraudsystem6802.service;

import com.softserve.academy.antifraudsystem6802.model.Role;
import com.softserve.academy.antifraudsystem6802.model.entity.User;
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
    private User defaultUser;

    @BeforeEach
    void setUp() {
        underTest =new UserService(userRepository, encoder);

        this.defaultUser = new User(
                null,
                "Artem",
                "fortamt",
                "password",
                false,
                null);
    }

    @Test
    void loadUserByUsername() {
        Mockito.when(userRepository.findByUsernameIgnoreCase(defaultUser.getUsername())).thenReturn(Optional.of(defaultUser));

        underTest.loadUserByUsername(defaultUser.getUsername());

        verify(userRepository).findByUsernameIgnoreCase(defaultUser.getUsername());
    }

    @Test
    void loadUserByUsernameNotExistedUsername() {
        assertThatThrownBy(() -> underTest.loadUserByUsername("UsernameNotExisted"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found: UsernameNotExisted");
    }

    @Test
    void canRegisterFirstUserAsAdministrator() {
        Mockito.when(userRepository.findByUsernameIgnoreCase(defaultUser.getUsername())).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(defaultUser)).thenReturn(defaultUser);

        //when
        underTest.register(defaultUser);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser).isEqualTo(defaultUser);
        assertThat(capturedUser.getRole()).isEqualTo(Role.ADMINISTRATOR);
        assertThat(capturedUser.isAccountNonLocked()).isTrue();
    }

    @Test
    void canRegisterOtherNotFirstUserAsMerchant() {
        //given
        given(userRepository.count()).willReturn(1L);
        Mockito.when(userRepository.findByUsernameIgnoreCase(defaultUser.getUsername())).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(defaultUser)).thenReturn(defaultUser);

        //when
        underTest.register(defaultUser);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser).isEqualTo(defaultUser);
        assertThat(capturedUser.getRole()).isEqualTo(Role.MERCHANT);
        assertThat(capturedUser.isAccountNonLocked()).isEqualTo(false);
    }

    @Test
    void cannotRegisterExistedUser() {
        Mockito.when(userRepository.findByUsernameIgnoreCase(defaultUser.getUsername())).thenReturn(Optional.ofNullable(defaultUser));

        //when
        assertThatThrownBy(() -> underTest.register(defaultUser))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void cannotRegisterWithWrongParameters() {
        //given
        defaultUser.setRole(Role.ADMINISTRATOR);
        Mockito.when(userRepository.findByUsernameIgnoreCase(defaultUser.getUsername())).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> underTest.register(defaultUser))
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
        defaultUser.setRole(Role.MERCHANT);
        RequestLock requestLock = new RequestLock();
        requestLock.setUsername(defaultUser.getUsername());
        requestLock.setOperation("UNLOCK");

        given(userRepository.findByUsernameIgnoreCase(defaultUser.getUsername())).willReturn(Optional.of(defaultUser));

        underTest.lock(requestLock);
        verify(userRepository).save(defaultUser);
        assertThat(defaultUser.isAccountNonLocked()).isTrue();
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
        defaultUser.setRole(Role.MERCHANT);

        RequestLock requestLock = new RequestLock();
        requestLock.setUsername(defaultUser.getUsername());
        requestLock.setOperation("LOCK");


        given(userRepository.findByUsernameIgnoreCase(defaultUser.getUsername())).willReturn(Optional.of(defaultUser));

        underTest.lock(requestLock);
        verify(userRepository).save(defaultUser);
        assertThat(defaultUser.isAccountNonLocked()).isFalse();
    }

    @Test
    void lockException() {
        defaultUser.setRole(Role.MERCHANT);

        RequestLock requestLock = new RequestLock();
        requestLock.setUsername(defaultUser.getUsername());
        requestLock.setOperation("WRONG");

        given(userRepository.findByUsernameIgnoreCase(defaultUser.getUsername())).willReturn(Optional.of(defaultUser));

        assertThatThrownBy(() -> underTest.lock(requestLock))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void lockException2() {
        defaultUser.setRole(Role.ADMINISTRATOR);

        RequestLock requestLock = new RequestLock();
        requestLock.setUsername(defaultUser.getUsername());
        requestLock.setOperation("LOCK");

        given(userRepository.findByUsernameIgnoreCase(defaultUser.getUsername())).willReturn(Optional.of(defaultUser));

        assertThatThrownBy(() -> underTest.lock(requestLock))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void delete() {
        Mockito.when(userRepository.deleteByUsernameIgnoreCase(defaultUser.getUsername())).thenReturn(1);

        boolean flag = underTest.delete(defaultUser.getUsername());
        verify(userRepository).deleteByUsernameIgnoreCase(defaultUser.getUsername());
        assertThat(flag).isTrue();
    }

    @Test
    void changeRole() {
        defaultUser.setRole(Role.MERCHANT);

        RoleRequest request = new RoleRequest();
        request.setUsername(defaultUser.getUsername());
        request.setRole(Role.SUPPORT);

        Mockito.when(userRepository.findByUsernameIgnoreCase(defaultUser.getUsername())).thenReturn(Optional.of(defaultUser));
        Mockito.when(userRepository.save(defaultUser)).thenReturn(defaultUser);

        underTest.changeRole(request);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();

        assertThat(defaultUser.getRole()).isEqualTo(request.getRole());
        assertThat(capturedUser.getRole()).isEqualTo(defaultUser.getRole());
        assertThat(defaultUser.getRole()).isEqualTo(Role.SUPPORT);
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
        defaultUser.setRole(Role.MERCHANT);
        RoleRequest request = new RoleRequest();
        request.setUsername(defaultUser.getUsername());
        request.setRole(Role.ADMINISTRATOR);

        Mockito.when(userRepository.findByUsernameIgnoreCase(request.getUsername())).thenReturn(Optional.of(defaultUser));

        assertThatThrownBy(() -> underTest.changeRole(request))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void changeRoleConflict() {
        defaultUser.setRole(Role.MERCHANT);

        RoleRequest request = new RoleRequest();
        request.setUsername(defaultUser.getUsername());
        request.setRole(Role.MERCHANT);

        Mockito.when(userRepository.findByUsernameIgnoreCase(request.getUsername())).thenReturn(Optional.of(defaultUser));

        assertThatThrownBy(() -> underTest.changeRole(request))
                .isInstanceOf(ResponseStatusException.class);
    }
}