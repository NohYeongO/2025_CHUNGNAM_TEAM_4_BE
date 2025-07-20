package com.chungnam.eco.user.service.dto;

import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.user.domain.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 사용자 전체 정보 DTO (password 제외)
 */
@Getter
@Builder
public class UserInfoDto {

    private final Long userId;
    private final String email;
    private final String nickname;
    private final UserRole role;
    private final Integer point;

    public static UserInfoDto from(User user) {
        return UserInfoDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .point(user.getPoint())
                .build();
    }

    public User toEntity() {
        return User.builder()
                .id(this.userId)
                .email(this.email)
                .nickname(this.nickname)
                .role(this.role)
                .point(this.point)
                .build();
    }
}
