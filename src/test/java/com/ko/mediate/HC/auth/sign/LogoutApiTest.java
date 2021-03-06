package com.ko.mediate.HC.auth.sign;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static com.ko.mediate.HC.auth.AccountFactory.*;

import com.ko.mediate.HC.HcApplicationTests;
import com.ko.mediate.HC.auth.application.AuthService;
import com.ko.mediate.HC.auth.application.request.SignInDto;
import com.ko.mediate.HC.auth.application.response.TokenDto;
import com.ko.mediate.HC.common.BaseApiTest;
import com.ko.mediate.HC.jwt.TokenStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("로그아웃 테스트")
public class LogoutApiTest extends BaseApiTest {
  @Autowired AuthService authService;
  @Autowired MockMvc mvc;
  @Autowired TokenStorage tokenStorage;

  @DisplayName("정상 로그아웃 테스트. 같은 토큰으로 로그인할 수 없음")
  @Test
  void logoutTest() throws Exception {
    // given
    SignInDto signInDto = createSignInDto(saveEmail, "1234");
    TokenDto dto = authService.signIn(signInDto);

    // when
    mvc.perform(delete("/api/logout").header(AUTHORIZATION, dto.getAccessToken()))
        .andExpect(status().isOk());

    // then
    assertThat(tokenStorage.getAccessTokenById(saveId)).isNull();
    assertThat(tokenStorage.getRefreshTokenById(saveId)).isNull();

    mvc.perform(delete("/api/logout").header(AUTHORIZATION, dto.getAccessToken()))
        .andExpect(status().isUnauthorized())
        .andDo(print());
    mvc.perform(post("/api/refresh").header("Refresh", dto.getRefreshToken()))
        .andExpect(status().isForbidden())
        .andDo(print());
  }
}
