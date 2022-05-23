package com.ko.mediate.HC.auth.application.request;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SignInDto {
  @ApiModelProperty(value = "계정 ID")
  @NotBlank(message = "계정 ID는 반드시 있어야 합니다.")
  private String accountId;

  @ApiModelProperty(value = "계정 비밀번호")
  @NotBlank(message = "비밀번호는 반드시 있어야 합니다.")
  private String password;

  @ApiModelProperty(value = "fcm token 값")
  @NotBlank(message = "fcm 토큰은 반드시 있어야 합니다.")
  private String fcmToken;
}