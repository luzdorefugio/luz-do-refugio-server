package com.luzdorefugio.domain;

import com.luzdorefugio.domain.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users") // Postgres não gosta da tabela chamada "user", por isso usamos "_user"
public class User implements UserDetails {

    @Id
    @UuidGenerator
    private UUID id;
    @Column(unique = true) // O email não pode ser repetido
    private String email;

    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    private Role role; // ADMIN ou USER
    private String phone;
    private String nif;
    private String address;
    private String city;
    private String zipCode;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Diz ao Spring que este user tem a permissão do Role dele
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email; // O nosso "username" é o email
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}