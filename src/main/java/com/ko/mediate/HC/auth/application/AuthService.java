package com.ko.mediate.HC.auth.application;

import com.ko.mediate.HC.auth.application.request.SignInDto;
import com.ko.mediate.HC.auth.domain.Account;
import com.ko.mediate.HC.auth.infra.JpaAccountRepository;
import com.ko.mediate.HC.common.exception.MediateNotFoundException;
import com.ko.mediate.HC.jwt.TokenProvider;
import com.ko.mediate.HC.tutoring.application.response.TokenDto;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component("userDetailsService")
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
  private final JpaAccountRepository accountRepository;
  private final TokenProvider tokenProvider;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  private Account findAccountByEmail(String email){
    return accountRepository.findAccountByEmail(email).orElseThrow(MediateNotFoundException::new);
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws AuthenticationException {
    return accountRepository
        .findAccountByEmail(email)
        .map(user -> createUser(user))
        .orElseThrow(() -> new BadCredentialsException("등록된 아이디가 없습니다."));
  }

  public TokenDto signIn(SignInDto dto) {
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(dto.getAccountEmail(), dto.getPassword());
    Authentication authentication =
        authenticationManagerBuilder.getObject().authenticate(authenticationToken);

    Account account = accountRepository.findAccountByEmail(dto.getAccountEmail()).orElseThrow();
    String refreshToken = tokenProvider.createRefreshToken(account.getId(), authentication);
    String accessToken = tokenProvider.createAccessToken(account.getId(), authentication);
    return new TokenDto(refreshToken, accessToken);
  }

  public User createUser(Account account) {
    if (!account.isActivated()) {
      throw new BadCredentialsException("활성화되지 않은 아이디입니다.");
    }
    List<GrantedAuthority> grantedAuthorities =
        Arrays.asList(new SimpleGrantedAuthority(account.getAuthority()));
    return new User(String.valueOf(account.getId()), account.getPassword(), grantedAuthorities);
  }
}
