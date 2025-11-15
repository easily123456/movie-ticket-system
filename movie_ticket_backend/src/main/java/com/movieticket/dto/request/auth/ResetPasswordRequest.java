package com.movieticket.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {
  @NotBlank(message = "用户名不能为空")
  private String username;

  @Email(message = "邮箱格式不正确")
  @NotBlank(message = "邮箱不能为空")
  private String email;
}
