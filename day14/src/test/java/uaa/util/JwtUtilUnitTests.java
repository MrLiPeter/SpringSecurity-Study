package uaa.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.val;
import org.h2.engine.UserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uaa.domain.Role;
import uaa.domain.User;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
public class JwtUtilUnitTests {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setup(){
        jwtUtil = new JwtUtil();
    }

    @Test
    public void givenUserDetails_thenCreateTokenSuccess(){
        val username = "user";
        val authoritities = Set.of(
                Role.builder()
                    .authority("ROLE_USER")
                    .build(),
                Role.builder()
                    .authority("ROLE_ADMIN")
                    .build()
        );
        val user = User.builder()
            .username(username)
            .authorities(authoritities)
            .build();
        //创建jwt
        val token = jwtUtil.createJwtToken(user);
        //解析jwt
        val parseClaims = Jwts.parserBuilder()
                .setSigningKey(JwtUtil.key)
                .build()
                .parseClaimsJws(token).getBody();
        assertEquals(username,parseClaims.getSubject(),"解析出来后subject应该是用户名");

    }
}
