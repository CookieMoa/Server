package com.example.securitywithredis.domain.auth.security;

import com.example.securitywithredis.domain.user.UserEntity;
import com.example.securitywithredis.domain.user.enums.AccountStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final UserEntity userEntity;

    public CustomUserDetails(UserEntity userEntity){
        this.userEntity = userEntity;
    }

    // 여러 권한을 가질 수 있으므로 Collection 사용
    // 스프링 시큐리티는 GrantedAuthority를 기반으로 권한을 처리하기 때문에 GrantedAuthority 인터페이스를 사용하여 권한 설정
    // -> 그냥 String으로 사용할 경우 시큐리티의 여러 보안 기능을 사용하지 못할 수 있음 (예: hasRole(), hasAuthority())
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {
                return userEntity.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return userEntity.getAccountStatus() != AccountStatus.LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return userEntity.getAccountStatus() == AccountStatus.ACTIVE;
    }
}
