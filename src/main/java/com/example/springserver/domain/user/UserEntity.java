package com.example.securitywithredis.domain.user;

import com.example.securitywithredis.global.common.entity.BaseEntity;
import com.example.securitywithredis.domain.user.enums.AccountStatus;
import com.example.securitywithredis.domain.user.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String username; // 실제로는 email값이 들어감

    @Column(nullable = false)
    private String password;

    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'ROLE_USER'")
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'ACTIVE'")
    private AccountStatus accountStatus;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Gender gender;

}
