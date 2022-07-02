package uaa.util;

import com.eatthepath.otp.HmacOneTimePasswordGenerator;
import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class TotpUtil {

    private static final long TIME_STEP = 60*5L;
    private static final int PASSWORD_LENGTH = 6;
    private KeyGenerator keyGenerator;
    private TimeBasedOneTimePasswordGenerator totp;

    /**
     * 初始化代码块，这种代码块的执行在构造函数之前
     * 准确说应该是Java编译器会把代码块拷贝到构造函数的最开始
     */
    {
        try {
            totp = new TimeBasedOneTimePasswordGenerator(Duration.ofSeconds(TIME_STEP),PASSWORD_LENGTH);
            keyGenerator = KeyGenerator.getInstance(totp.getAlgorithm());
            //SHA-1 and SHA-256 需要64字节 （512位）的key
            //SHA512 需要128字节（1024位）的key
            keyGenerator.init(512);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            log.error("没有找到算法{}",e.getLocalizedMessage());
        }
    }

    /**
     * 生成Totp
     * @param key
     * @param time
     * @return
     * @throws InvalidKeyException
     */
    public String createTotp(Key key, Instant time) throws InvalidKeyException {
        val password = totp.generateOneTimePassword(key,time);
        val format = "%0"+PASSWORD_LENGTH+"d";
        return String.format(format,password);
    }

    public Optional<String> createTotp(String strKey){
        try {
            return Optional.of(createTotp(decodeKeyFromString(strKey),Instant.now()));
        } catch (InvalidKeyException e) {
            return Optional.empty();
        }
    }

    /**
     * 验证Totp
     * @param code
     * @return
     */
    public boolean verifyTotp(Key key,String code) throws InvalidKeyException {
        val now = Instant.now();
        return code.equals(createTotp(key,now));
    }

    public Key generateKey(){
        return keyGenerator.generateKey();
    }

    private String encodeKeyToString(Key key){
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public String encodeKeyToString(){
        return encodeKeyToString(generateKey());
    }

    public Key decodeKeyFromString(String strKey){
        return new SecretKeySpec(Base64.getDecoder().decode(strKey),totp.getAlgorithm());
    }

    public long getTimeStepInSeconds(){
        return TIME_STEP;
    }
}
