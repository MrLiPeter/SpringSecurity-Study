package uaa.util;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import uaa.config.AppProperties;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Component
public class JwtUtil {

    //用于签名访问令牌的秘钥
    public static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    //用于签名刷新令牌的秘钥
    public static final Key refreshkey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    private final AppProperties appProperties;

    public String createAccessToken(UserDetails userDetails){
        return createJwtToken(userDetails,appProperties.getJwt().getAccessTokenExpireTime(),key);
    }

    public String createRefreshToken(UserDetails userDetails){
        return createJwtToken(userDetails,appProperties.getJwt().getRefreshTokenExpireTime(),refreshkey);
    }

    public String createAccessTokenWithRefreshToken(String token){
        return parseClaims(token,refreshkey)
                .map(claims -> Jwts.builder()
                        .setClaims(claims)
                        .setExpiration(new Date(System.currentTimeMillis()+appProperties.getJwt().getAccessTokenExpireTime()))
                        .setIssuedAt(new Date())
                        .signWith(key,SignatureAlgorithm.HS512)
                        .compact()
                ).orElseThrow(()->new AccessDeniedException("访问被拒绝"));
    }

    public Optional<Claims> parseClaims(String token,Key key){
        try {
            val claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            return Optional.of(claims);
        }catch (Exception e){
            return Optional.empty();
        }
    }

    public boolean validateAccessTokenWithoutExpiration(String token){
        return validateToken(token,key,false);
    }

    public boolean validateAccessToken(String token){
        return validateToken(token,key,true);
    }

    public boolean validateRefreshToken(String token){
        return validateToken(token,refreshkey,true);
    }

    public boolean validateToken(String token,Key key,boolean isExpiredInvalid){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parse(token);
            return true;
        }catch (ExpiredJwtException | SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e){
            if(e instanceof ExpiredJwtException){
                return !isExpiredInvalid;
            }
            return false;
        }
    }

    public String createJwtToken(UserDetails userDetails,long timeToExpire,Key key){
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setId("lxh")
                .claim("authorities",userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(toList()))
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now+timeToExpire))
                .signWith(key,SignatureAlgorithm.HS512)
                .compact();
    }
}
