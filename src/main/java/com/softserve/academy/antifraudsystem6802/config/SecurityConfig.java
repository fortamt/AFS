package com.softserve.academy.antifraudsystem6802.config;

import com.softserve.academy.antifraudsystem6802.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //@formatter:off
        http.httpBasic()
                    .authenticationEntryPoint(restAuthenticationEntryPoint()) // Handles auth error
                .and()
                    .csrf().disable()
                    .headers()
                        .frameOptions().disable() // for Postman, the H2 console
                .and()
                    .authorizeRequests() // manage access
                        .mvcMatchers(HttpMethod.GET, "/api/auth/list").hasAnyRole(Role.ADMINISTRATOR.name(), Role.SUPPORT.name())
                        .mvcMatchers(HttpMethod.DELETE, "/api/auth/user/*").hasAnyRole(Role.ADMINISTRATOR.name())
                        .mvcMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasAnyRole(Role.MERCHANT.name())
                        .mvcMatchers(HttpMethod.POST, "/api/antifraud/access").hasAnyRole(Role.ADMINISTRATOR.name())
                        .mvcMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                        .mvcMatchers("/actuator/shutdown").permitAll() // needs to run test
                .and()
                    .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session
        //@formatter:on
    }

    @Bean
    AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return (request, response, authException) -> response.sendError(
                HttpStatus.UNAUTHORIZED.value(), authException.getMessage());
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }
}
