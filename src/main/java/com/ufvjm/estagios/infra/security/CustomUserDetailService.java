package com.ufvjm.estagios.infra.security;

import com.ufvjm.estagios.entities.Usuario;
import com.ufvjm.estagios.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UsuarioRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = this.repository.findByEmailInstitucional(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!usuario.isAtivo()) {
            throw new UsernameNotFoundException("Usuário não ativado. Verifique seu e-mail.");
        }

        var authorities = Collections.singletonList(new SimpleGrantedAuthority(usuario.getRole().name()));

        return new User(usuario.getEmailInstitucional(), usuario.getSenha(), authorities);
    }
}
