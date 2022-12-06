package com.jinjjaseoul.common;

import com.jinjjaseoul.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SessionUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String email;
    private Role role;
}