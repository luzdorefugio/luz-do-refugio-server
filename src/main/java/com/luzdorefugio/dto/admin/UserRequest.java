package com.luzdorefugio.dto.admin;

import com.luzdorefugio.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private UUID id;
    private String name;
    private String email;
    private Role role;
    private String password;
}