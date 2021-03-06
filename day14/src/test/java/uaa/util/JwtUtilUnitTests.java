package uaa.util;

import io.jsonwebtoken.Jwts;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uaa.config.AppProperties;
import uaa.domain.Role;
import uaa.domain.User;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
public class JwtUtilUnitTests {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setup(){
        jwtUtil = new JwtUtil(new AppProperties());
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
        val token = jwtUtil.createAccessToken(user);
        //解析jwt
        val parseClaims = Jwts.parserBuilder()
                .setSigningKey(JwtUtil.key)
                .build()
                .parseClaimsJws(token).getBody();
        assertEquals(username,parseClaims.getSubject(),"解析出来后subject应该是用户名");
    }

}
