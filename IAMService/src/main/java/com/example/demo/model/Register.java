package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Register {
    private String userName;
    private String email;
    private String password;
    private String doB;
    private String address;
    private String mobile;
}
