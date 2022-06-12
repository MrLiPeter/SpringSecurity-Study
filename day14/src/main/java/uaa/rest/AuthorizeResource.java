package uaa.rest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import uaa.domain.Auth;
import uaa.domain.User;
import uaa.domain.dto.LoginDto;
import uaa.domain.dto.UserDto;
import uaa.exception.DuplicateProblem;
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
    public String register(@Valid @RequestBody UserDto userDto){
        //todo:1、检查username、email、mobile都是唯一的、所以要查询数据库确保唯一
        if (userService.isUsernameExisted(userDto.getUsername())){
            throw new DuplicateProblem("用户名已经存在");
        }
        if (userService.isMobileExisted(userDto.getMobile())){
            throw new DuplicateProblem("手机号已经存在");
        }
        if (userService.isEmailExisted(userDto.getEmail())){
            throw new DuplicateProblem("电子邮箱已经存在");
        }
        //todo:2、我们需要userDto转换为User,给一个默认角色(Role_USER),然后保存。
        val user = User.builder()
                .username(userDto.getUsername())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .mobile(userDto.getMobile())
                .password(userDto.getPassword())
                .build();
        //TODO:3、我们给一个默认角色(ROLE_USER),然后保存
        userService.register(user);
        return "注册成功";
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
