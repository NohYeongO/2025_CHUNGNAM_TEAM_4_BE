package com.chungnam.eco.common.exception;

import lombok.Getter;

@Getter
public class MissionNotFoundExcption extends RuntimeException {
  private final ErrorCode errorCode;

  public MissionNotFoundExcption() {
    this.errorCode = ErrorCode.MISSION_NOT_FOUND;
  }

  public MissionNotFoundExcption(String message) {
    super(message);
    this.errorCode = ErrorCode.MISSION_NOT_FOUND;
  }
}
