package me.artemiyulyanov.uptodate.controllers.api.auth.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerifyCodeRequest {
    private String email, code;
}