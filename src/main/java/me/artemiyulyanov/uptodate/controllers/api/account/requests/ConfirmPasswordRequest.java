package me.artemiyulyanov.uptodate.controllers.api.account.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmPasswordRequest {
    private String password, repeatedPassword;
}