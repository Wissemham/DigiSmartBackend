package com.stage.digibackend.dto;

import lombok.Data;

@Data
public class PasswordRessetRequestDto {
    private String phoneNumber;
    private String userName;
    private String oneTimePassword;
}
