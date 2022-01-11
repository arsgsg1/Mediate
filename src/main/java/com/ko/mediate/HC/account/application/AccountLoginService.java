package com.ko.mediate.HC.account.application;

import com.ko.mediate.HC.account.domain.Account;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component("userDetailsService")
@RequiredArgsConstructor
public class AccountLoginService implements UserDetailsService {
  private final AccountRepository accountRepository;

  @Override
  public UserDetails loadUserByUsername(String accountId) throws AuthenticationException {
    return accountRepository
        .findByAccountId(accountId)
        .map(user -> createUser(accountId, user))
        .orElseThrow(() -> new BadCredentialsException("등록된 아이디가 없습니다."));
  }

  private User createUser(String accountId, Account account) {
    if (!account.isActivated()) {
      throw new RuntimeException("활성화되지 않은 아이디입니다.");
    }
    List<GrantedAuthority> grantedAuthorities =
        account.getAuthority().stream()
            .map(authority -> new SimpleGrantedAuthority(authority))
            .collect(Collectors.toList());
    return new User(account.getId(), account.getPassword(), grantedAuthorities);
  }
}
