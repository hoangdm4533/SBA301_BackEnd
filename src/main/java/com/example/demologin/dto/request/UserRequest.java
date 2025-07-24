package com.example.demologin.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserRequest {
    public String username;
    public String email;
    public String password;
    public String fullname;
    public String phone;
    public String address;
    public LocalDate dateOfBirth;
    public String identity_Card;

}