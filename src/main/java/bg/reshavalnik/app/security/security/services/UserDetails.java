package bg.reshavalnik.app.security.security.services;

import bg.reshavalnik.app.security.domain.Role;
import bg.reshavalnik.app.security.dto.projection.UserDetailProjection;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@ToString(exclude = "password")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDetails implements org.springframework.security.core.userdetails.UserDetails {
    static final long serialVersionUID = 1L;

    @EqualsAndHashCode.Include String id;

    String username;

    @JsonIgnore String password;

    Collection<? extends GrantedAuthority> authorities;

    public static UserDetails build(UserDetailProjection user) {
        Role r = user.getRoles();
        String springAuthority = "ROLE_" + r.name();
        GrantedAuthority ga = new SimpleGrantedAuthority(springAuthority);

        return UserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(List.of(ga))
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
