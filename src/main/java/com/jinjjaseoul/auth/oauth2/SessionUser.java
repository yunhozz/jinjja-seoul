package com.jinjjaseoul.auth.oauth2;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SessionUser implements Serializable {

    private String email;
    private String name;
    private String imageUrl;
}