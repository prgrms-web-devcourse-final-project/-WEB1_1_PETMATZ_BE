package com.petmatz.user.entity;

import com.petmatz.domain.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity(name = "User")
@Table(name = "User")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "account_id", nullable = false, unique = true)
    private String accountId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "email")
    private String email;

    @Column(name = "profile_img")
    private String profileImg;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_role")
    private LoginRole loginRole; // ROLE_USER, ROLE_ADMIN

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type")
    private LoginType loginType; // 'Normal', 'Kakao'

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role; //'Dol' or 'Mat'

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender; // 'Male', 'Female'

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_size", nullable = false)
    private PreferredSize preferredSize; // 'Small', 'Medium', 'Large'

    @Column(name = "introduction")
    private String introduction;

    @Column(name = "is_care_available", nullable = false)
    private Boolean isCareAvailable = false;

    @Column(name = "is_registered")
    private Boolean isRegistered = false;

    @Column(name = "recommendation_count")
    private Integer recommendationCount = 0;

    @Column(name = "care_completion_count")
    private Integer careCompletionCount = 0;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "time_wage",nullable = false)
    private Integer timeWage = 0;

    @Column(name = "month_wage",nullable = false)
    private Integer monthWage = 0;

    public enum LoginRole{
        ROLE_USER, ROLE_ADMIN
    }

    public enum Role {
        Dol, Mat
    }

    public enum LoginType {
        Normal, Kakao
    }

    public enum Gender {
        Male, Female
    }

    public enum PreferredSize {
        Small, Medium, Large
    }
}