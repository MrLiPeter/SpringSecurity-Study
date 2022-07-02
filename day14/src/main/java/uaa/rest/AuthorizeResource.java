package uaa.rest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import uaa.domain.Auth;
import uaa.domain.User;
import uaa.domain.dto.LoginDto;
import uaa.domain.dto.UserDto;
import uaa.exception.DuplicateProblem;
import uaa.service.UserCacheService;
import uaa.service.UserService;
import uaa.util.JwtUtil;

import javax.validation.Valid;

@RequestMapping("/authorize")
@RestController
@RequiredArgsConstructor
public class AuthorizeResource {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserCacheService userCacheService;

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
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) throws AuthenticationException {

        return  userService.findOptionalByUsernameAndPassword(loginDto.getUserName(),loginDto.getPassword())
                .map(user -> {
                    // todo 1、升级密码编码
                    // todo 2、验证
                    // todo 3、判断usingMfa,如果是false,直接返回Token
                    if(!user.isUsingMfa()){
                        return ResponseEntity.ok().body(userService.login(loginDto.getUserName(),loginDto.getPassword()));
                    }
                    // todo 4、使用多因子认证
                    val mfaId = userCacheService.cacheUser(user);
                    // todo 5、"X-Authenticate":"mfa","realm="+mfaId
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .header("X-Authenticate","mfa","realm"+mfaId)
                            .build();
                }).orElseThrow(()->new BadCredentialsException("用户名或密码错误！"));
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
