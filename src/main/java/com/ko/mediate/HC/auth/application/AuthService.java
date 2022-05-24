package com.ko.mediate.HC.auth.application;

import com.ko.mediate.HC.auth.application.request.SignInDto;
import com.ko.mediate.HC.auth.domain.Account;
import com.ko.mediate.HC.auth.infra.JpaAccountRepository;
import com.ko.mediate.HC.auth.resolver.UserInfo;
import com.ko.mediate.HC.common.exception.MediateNotFoundException;
import com.ko.mediate.HC.jwt.CustomUserDetails;
import com.ko.mediate.HC.jwt.TokenProvider;
import com.ko.mediate.HC.auth.application.response.TokenDto;
import com.ko.mediate.HC.jwt.TokenStorage;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
  private final JpaAccountRepository accountRepository;
  private final TokenProvider tokenProvider;
  private final TokenStorage tokenStorage;

  private Account findAccountByEmail(String email) {
    return accountRepository.findAccountByEmail(email).orElseThrow(MediateNotFoundException::new);
  }

  @Override
  public CustomUserDetails loadUserByUsername(String email) throws AuthenticationException {
    return accountRepository
        .findAccountByEmail(email)
        .map(user -> createUser(user))
        .orElseThrow(() -> new BadCredentialsException("등록된 아이디가 없습니다."));
  }

  public TokenDto signIn(SignInDto dto) {
    Account account = findAccountByEmail(dto.getAccountEmail());
    String refreshToken =
        tokenProvider.createRefreshToken(account.getId(), dto.getAccountEmail(), dto.getRoleType());
    String accessToken =
        tokenProvider.createAccessToken(account.getId(), dto.getAccountEmail(), dto.getRoleType());
    tokenStorage.saveRefreshToken(refreshToken, String.valueOf(account.getId()));
    return new TokenDto(refreshToken, accessToken);
  }

  public CustomUserDetails createUser(Account account) {
    if (!account.isActivated()) {
      throw new BadCredentialsException("활성화되지 않은 아이디입니다.");
    }
    Set<GrantedAuthority> grantedAuthorities =
        Set.of(new SimpleGrantedAuthority(account.getAuthority()));
    return new CustomUserDetails(
        String.valueOf(account.getId()), account.getPassword(), grantedAuthorities);
  }

  public TokenDto reissueAccessTokenByRefreshToken(String refreshToken) {
    tokenProvider.validateToken(refreshToken);
    UserInfo userInfo = tokenProvider.getUserInfoFromToken(refreshToken);
    String accessToken =
        tokenProvider.createAccessToken(
            userInfo.getAccountId(), userInfo.getAccountEmail(), userInfo.getAuthority());
    tokenStorage.saveAccessToken(accessToken, String.valueOf(userInfo.getAccountId()));
    return new TokenDto(null, accessToken);
  }
}
