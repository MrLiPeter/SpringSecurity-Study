package uaa.rest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import uaa.domain.Auth;
import uaa.domain.dto.LoginDto;
import uaa.domain.dto.UserDto;
import uaa.service.UserService;
import uaa.util.JwtUtil;

import javax.validation.Valid;

@RequestMapping("/authorize")
@RestController
@RequiredArgsConstructor
public class AuthorizeResource {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public UserDto register(@Valid @RequestBody UserDto userDto){
        return userDto;
    }

    @PostMapping("/token")
    public Auth login(@Valid @RequestBody LoginDto loginDto) throws AuthenticationException {
        return userService.login(loginDto.getUserName(),loginDto.getPassword());
    }

    @PostMapping("/token/refresh")
    public Auth refreshToken(@RequestHeader(name="Authorization") String authorization,
                             @RequestParam String refreshToken) throws AccessDeniedException{
        val prefix = "Bearer";
        val accessToken = authorization.replace(prefix,"");
        if (jwtUtil.validateRefreshToken(refreshToken) && jwtUtil.validateAccessToken(accessToken)){
            return new Auth(jwtUtil.createAccessTokenWithRefreshToken(refreshToken),refreshToken);
        }
        throw new AccessDeniedException("访问被拒绝");
    }

}
