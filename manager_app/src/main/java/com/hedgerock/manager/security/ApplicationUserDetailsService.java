package com.hedgerock.manager.security;

import com.hedgerock.manager.entities.Authority;
import com.hedgerock.manager.repository.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationUserDetailsService implements UserDetailsService {

    private final ApplicationUserRepository repository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.repository.findByUsername(username).map(user ->
                        User.builder()
                                .username(user.getUsername())
                                .password(user.getPassword())
                                .authorities(user
                                        .getAuthorities()
                                        .stream()
                                        .map(Authority::getAuthority)
                                        .map(SimpleGrantedAuthority::new).toList()
                                )
                                .build()
                ).orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));
    }


}
