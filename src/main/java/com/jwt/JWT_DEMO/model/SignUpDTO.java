package com.jwt.JWT_DEMO.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SignUpDTO {

    private String username;
    private  String password;
    private String role;
    private boolean isEnabled;

}
