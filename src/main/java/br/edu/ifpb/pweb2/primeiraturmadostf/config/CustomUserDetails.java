package br.edu.ifpb.pweb2.primeiraturmadostf.config;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

    private final String matricula;
    private final String senha;
    private final String nome;
    private final boolean ativo;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(String matricula, String senha, String nome, boolean ativo,
            Collection<? extends GrantedAuthority> authorities) {
        this.matricula = matricula;
        this.senha = senha;
        this.nome = nome;
        this.ativo = ativo;
        this.authorities = authorities;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return matricula;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }
}
