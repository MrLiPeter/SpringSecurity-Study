package uaa.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uaa.config.Constants;
import uaa.domain.Auth;
import uaa.domain.User;
import uaa.domain.dto.LoginDto;
import uaa.repository.RoleRepo;
import uaa.repository.UserRepo;
import uaa.util.JwtUtil;
import uaa.util.TotpUtil;

import java.util.Optional;
import java.util.Set;


@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TotpUtil totpUtil;

    @Transactional
    public User register(User user){
        return roleRepo.findOptionalByAuthority(Constants.ROLE_USER)
                .map(role -> {
                    val userToSave = user.
                            withAuthorities(Set.of(role))
                            .withPassword(passwordEncoder.
                                    encode(user.getPassword()))
                            .withMfaKey(totpUtil.encodeKeyToString());
                    return userRepo.save(userToSave);
                }).orElseThrow();
    }


    public Auth login(String username,String passsword) throws AuthenticationException {
        return userRepo.findOptionalByUsername(username)
                .filter(user->passwordEncoder.matches(passsword,user.getPassword()))
                .map(user -> new Auth(
                        jwtUtil.createAccessToken(user),
                        jwtUtil.createRefreshToken(user)
                )).orElseThrow(()->new BadCredentialsException("用户名密码错误"));
    }

    public Optional<User> findOptionalByUsernameAndPassword(String userName, String password) {
        return userRepo.findOptionalByUsername(userName)
                .filter(user -> passwordEncoder.
                        matches(password,user.getPassword()));
    }
    public UserDetails updatePassword(User user, String newPassword){
        return userRepo.findOptionalByUsername(user.getUsername())
                .map(u->
                        (User)userRepo.save(u.withPassword(newPassword))
                )
                .orElse(user);
    }

    public Optional<String> createTotp(String key){
        return totpUtil.createTotp(key);
    }

    /**
     * 判断用户名是否存在
     * @param username
     * @return
     */
    public boolean isUsernameExisted(String username){
        return userRepo.countByUsername(username)>0;
    }

    /**
     * 判断电子邮件是否存在
     * @param username
     * @return
     */
    public boolean isEmailExisted(String username){
        return userRepo.countByEmail(username)>0;
    }

    /**
     * 判断电子邮件是否存在
     * @param username
     * @return
     */
    public boolean isMobileExisted(String username){
        return userRepo.countByMobile(username)>0;
    }


}
