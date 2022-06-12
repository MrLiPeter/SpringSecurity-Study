package uaa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uaa.domain.Auth;
import uaa.domain.dto.LoginDto;
import uaa.repository.UserRepo;
import uaa.util.JwtUtil;



@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Auth login(String username,String passsword) throws AuthenticationException {
        return userRepo.findOptionalByUsername(username)
                .filter(user->passwordEncoder.matches(passsword,user.getPassword()))
                .map(user -> new Auth(
                        jwtUtil.createAccessToken(user),
                        jwtUtil.createRefreshToken(user)
                )).orElseThrow(()->new BadCredentialsException("用户名密码错误"));
    }
}
