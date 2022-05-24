package com.ko.mediate.HC.AuthTest;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;
import static com.ko.mediate.HC.AuthTest.AccountFactory.*;

import com.ko.mediate.HC.HcApplicationTests;
import com.ko.mediate.HC.auth.application.AuthService;
import com.ko.mediate.HC.auth.exception.AccountPasswordNotEqualsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SignInServiceTest extends HcApplicationTests {
  @Autowired AuthService authService;

  @DisplayName("비밀번호가 틀린 예외")
  @Test
  void incorrectPasswordTest() {
    assertThatThrownBy(() -> authService.signIn(createSignInDto("test@google.com", "아무비밀번호")))
        .isInstanceOf(AccountPasswordNotEqualsException.class);
  }
}
