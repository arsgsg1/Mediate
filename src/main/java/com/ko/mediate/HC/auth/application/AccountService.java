package com.ko.mediate.HC.auth.application;

import com.ko.mediate.HC.auth.application.request.SignUpDto;
import com.ko.mediate.HC.common.ErrorCode;
import com.ko.mediate.HC.common.exception.MediateAlreadyExistException;
import com.ko.mediate.HC.common.exception.MediateIllegalStateException;
import com.ko.mediate.HC.common.exception.MediateNotFoundException;
import com.ko.mediate.HC.tutoring.application.RoleType;
import com.ko.mediate.HC.auth.domain.Account;
import com.ko.mediate.HC.auth.infra.JpaAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
  private final JpaAccountRepository accountRepository;
  private final PasswordEncoder passwordEncoder;

  public void checkExistEmail(String email) {
    if (accountRepository.existsByEmail(email)) {
      throw new MediateIllegalStateException(ErrorCode.EMAIL_ALREADY_EXIST);
    }
  }

  public void saveAccount(SignUpDto dto) {
    checkExistEmail(dto.getEmail());
    Account account =
        Account.builder()
            .email(dto.getEmail())
            .password(passwordEncoder.encode(dto.getPassword()))
            .name(dto.getName())
            .phoneNum(dto.getPhoneNum())
            .role(RoleType.ROLE_USER)
            .build();
    accountRepository.save(account);
  }

  public Account getAccountByEmail(String email) {
    return accountRepository
        .findAccountByEmail(email)
        .orElseThrow(() -> new MediateAlreadyExistException(ErrorCode.EMAIL_ALREADY_EXIST));
  }
}
