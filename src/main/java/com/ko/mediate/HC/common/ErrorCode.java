package com.ko.mediate.HC.common;

import lombok.Getter;

@Getter
public enum ErrorCode {
  EMAIL_ALREADY_EXIST("ERR_0001", "중복된 이메일입니다."),
  ENTITY_NOT_FOUND("ERR_0002", "해당 ID의 엔티티가 없습니다."),
  NO_REFRESH_TOKEN("ERR_0003", "리프레쉬 토큰이 없습니다."),
  ALREADY_TUTOR_OR_TUTEE("ERR_0004", "이미 튜터이거나 튜티입니다."),
  IMAGE_EXCEED("ERR_0005", "이미지는 최대 5개까지 첨부가능합니다."),
  NO_ACCESS_TOKEN("ERR_0006", "액세스 토큰이 없습니다."),
  TUTORING_NO_WAIT_STATE("ERR_0007", "튜터링이 수락 대기 중 상태가 아닙니다."),
  NO_TUTORING_MEMBER("ERR_0008", "튜터링 멤버가 아닙니다."),
  TUTORING_CANCEL_STATE("ERR_0009", "학습 중이거나 대기 중이어야 합니다."),
  INCORRECT_PASSWORD("ERR_0010", "비밀번호가 일치하지 않습니다."),
  INVALID_TOKEN("ERR_0011", "유효하지 않은 토큰입니다."),
  IMAGE_CONVERT_FAILED("ERR_0012", "유효하지 않은 이미지입니다."),
  UNSUPPORT_IMAGE_TYPE("ERR_0013", "지원하지 않는 미디어타입입니다.");

  private final String code;
  private final String description;

  ErrorCode(String code, String description) {
    this.code = code;
    this.description = description;
  }
}
