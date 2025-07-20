package com.chungnam.eco.common.exception;

import lombok.Getter;

@Getter
public class MissionNotFoundExcption extends RuntimeException {
  private final ErrorCode errorCode;
  public MissionNotFoundExcption() {
    this.errorCode = ErrorCode.USER_NOT_FOUND;
  }
}
