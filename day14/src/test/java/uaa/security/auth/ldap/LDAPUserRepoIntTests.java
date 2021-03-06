package uaa.security.auth.ldap;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.ldap.DataLdapTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
@DataLdapTest
public class LDAPUserRepoIntTests {
    @Autowired
    LDAPUserRepo ldapUserRepo;

    @Test
    public void givenUsernameAndPassword_ThenFindUserSuccess(){
        String username = "zhaoliu";
        String password = "123";
        val user = ldapUserRepo.findByUsernameAndPassword(username, password);
        assertTrue(user.isPresent());
    }

}
