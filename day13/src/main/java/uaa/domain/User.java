package uaa.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@With
@Data
@Entity
@Table(name = "lxh_users")
public class User implements UserDetails,Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50,unique = true,nullable = false)
    private String username;
    @Column(length = 50)
    private String name;

    @JsonIgnore //返回json给前端时忽略该属性
    @Column(name = "password_hash",length = 255,nullable = false)
    private String password;
    @Column(length = 255,unique = true,nullable = false)
    private String email;
    @Column(nullable = false)
    private boolean enabled;
    @Column(name = "account_non_expired",nullable = false)
    private boolean accountNonExpired;
    @Column(name = "account_non_locked",nullable = false)
    private boolean accountNonLocked;
    @Column(name = "credentials_non_expired",nullable = false)
    private boolean credentialsNonExpired;

    @ManyToMany
    @Fetch(FetchMode.JOIN)
    @JoinTable(name = "lxh_users_roles",
    joinColumns = {@JoinColumn(name = "user_id",referencedColumnName = "id")},
    inverseJoinColumns = {@JoinColumn(name = "role_id",referencedColumnName = "id")})
    private Set<Role> authorities;


}
