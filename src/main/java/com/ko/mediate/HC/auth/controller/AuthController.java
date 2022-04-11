package com.ko.mediate.HC.auth.controller;

import com.ko.mediate.HC.auth.application.AccountService;
import com.ko.mediate.HC.auth.application.request.SignupDto;
import com.ko.mediate.HC.firebase.application.FirebaseCloudService;
import com.ko.mediate.HC.jwt.JwtFilter;
import com.ko.mediate.HC.jwt.TokenProvider;
import com.ko.mediate.HC.tutoring.application.dto.response.TokenDto;
import com.ko.mediate.HC.tutoring.application.dto.request.LoginDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
@Api(tags = {"로그인용 api"})
public class AuthController {
  private final TokenProvider tokenProvider;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final FirebaseCloudService firebaseCloudService;
  private final AccountService accountService;

  @PostMapping("/auth")
  @ApiOperation(
      value = "로그인",
      notes = "성공시 jwt 토큰을 헤더와 응답 값에 넣어 반환합니다.",
      produces = "application/json",
      response = TokenDto.class)
  public ResponseEntity<TokenDto> Authorize(@Valid @RequestBody LoginDto loginDto) {
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(loginDto.getId(), loginDto.getPassword());
    Authentication authentication =
        authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    String jwt = tokenProvider.createToken(authentication);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

    firebaseCloudService.renewFcmToken(loginDto);
    return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
  }

  @PostMapping("/signup")
  @ApiOperation(
      value = "회원가입",
      notes = "계정의 회원가입을 하는 메서드입니다.")
  public ResponseEntity Signup(@Valid @RequestBody SignupDto dto) {
    accountService.saveAccount(
        dto.getId(), dto.getPassword(), dto.getName(), dto.getPhoneNum(), dto.getRoleType());
    return ResponseEntity.ok("회원가입이 완료되었습니다.");
  }
}
