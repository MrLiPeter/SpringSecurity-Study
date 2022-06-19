package uaa.util;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uaa.common.BaseIntegrationTest;

import java.security.InvalidKeyException;
import java.time.Instant;
import java.util.Optional;

import static java.time.Instant.now;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;

public class TotpUtilTest extends BaseIntegrationTest {

    @Autowired
    private  TotpUtil totpUtil;


    @Test
    public void createTotp_thenVerifyTotpSuccess() throws InvalidKeyException {
        // todo :1 生成一个key
        TotpUtil totpUtil = new TotpUtil();
        val key = totpUtil.generateKey();
        //todo：2、通过key生成一个totp
        String first = totpUtil.createTotp(key, now());
        System.out.println(first);
        //todo 3、第一次验证totp成功
        System.out.println(totpUtil.verifyTotp(key,first));
        assertTrue(totpUtil.verifyTotp(key,first),"第一次验证成功");
    }

    public static void main(String[] args) throws InvalidKeyException {
        TotpUtil totpUtil = new TotpUtil();
        val key = totpUtil.generateKey();
        //todo：2、通过key生成一个totp
        String first = totpUtil.createTotp(key, now());
        System.out.println(first);
        //todo 3、第一次验证totp成功
        System.out.println(totpUtil.verifyTotp(key,first));
        assertTrue(totpUtil.verifyTotp(key,first),"第一次验证成功");
    }
}
