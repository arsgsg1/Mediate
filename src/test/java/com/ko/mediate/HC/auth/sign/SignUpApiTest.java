package com.ko.mediate.HC.auth.sign;

import static com.ko.mediate.HC.auth.AccountFactory.createSignUpDto;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;
import static com.ko.mediate.HC.auth.AccountFactory.*;

import com.ko.mediate.HC.auth.application.AccountService;
import com.ko.mediate.HC.auth.application.request.SignUpDto;
import com.ko.mediate.HC.auth.domain.Account;
import com.ko.mediate.HC.common.BaseApiTest;
import com.ko.mediate.HC.common.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

public class SignUpApiTest extends BaseApiTest {
  @Autowired MockMvc mvc;
  @Autowired AccountService accountService;
  final String existEmail = "test123@naver.com";

  @DisplayName("회원가입 성공 테스트")
  @Test
  void signUpTest() throws Exception {
    // given
    SignUpDto dto = createSignUpDto("test@naver.com");

    // when, then
    mvc.perform(
            post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated());

    Account account = accountRepository.findAccountByEmail(dto.getEmail()).get();
    assertThat(account.getEmail()).isEqualTo(dto.getEmail());
    assertThat(account.getName()).isEqualTo(dto.getName());
    assertThat(account.getPhoneNum()).isEqualTo(dto.getPhoneNum());
  }

  @DisplayName("회원가입 이메일 중복 예외")
  @Test
  void validateEmailTest() throws Exception {
    // given
    SignUpDto dto = createSignUpDto(existEmail);
    accountService.saveAccount(dto);

    // when, then
    mvc.perform(
            post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(containsString(ErrorCode.EMAIL_ALREADY_EXIST.getCode())));
  }
}
