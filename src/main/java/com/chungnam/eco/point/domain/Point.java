package com.chungnam.eco.point.domain;

import com.chungnam.eco.common.entity.BaseTimeEntity;
import com.chungnam.eco.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "point")
public class Point extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String shopName;

    private Integer usedPoints;

    private Integer totalPoints;

    @Builder
    public Point(User user, String shopName, Integer usedPoints, Integer totalPoints) {
        this.user = user;
        this.shopName = shopName;
        this.usedPoints = usedPoints;
        this.totalPoints = totalPoints;
    }
}
