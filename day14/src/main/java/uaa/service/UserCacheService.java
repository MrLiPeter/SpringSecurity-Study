package uaa.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import uaa.config.Constants;
import uaa.domain.User;
import uaa.util.CryptoUtil;
import uaa.util.TotpUtil;

import java.security.InvalidKeyException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class UserCacheService {
    private final RedissonClient redisson;
    private final TotpUtil totpUtil;
    private final CryptoUtil cryptoUtil;

    public String cacheUser(User user){
        val mfaId = cryptoUtil.randomAlphanumeric(12);
        RMapCache<String,User> cache = redisson.getMapCache(Constants.CACHE_MFA);
        if(!cache.containsKey(mfaId)){
            cache.put(mfaId,user,totpUtil.getTimeStepInSeconds(), TimeUnit.SECONDS);
        }
        return mfaId;
    }

    public Optional<User> retrieveUser(String mfaId){
        RMapCache<String,User> cache = redisson.getMapCache(Constants.CACHE_MFA);
        if(cache.containsKey(mfaId)){
            return Optional.of(cache.get(mfaId));
        }
        return Optional.empty();
    }

    public Optional<User> verifyTotp(String mfaId,String code){
        RMapCache<String,User> cache = redisson.getMapCache(Constants.CACHE_MFA);
        if(!cache.containsKey(mfaId) || cache.get(mfaId) == null){
            return Optional.empty();
        }
        val cachedUser = cache.get(mfaId);
        try {
            val isValid = totpUtil.verifyTotp(totpUtil.decodeKeyFromString(cachedUser.getMfaKey()),code);
            if(!isValid){
                return Optional.empty();
            }
            cache.remove(mfaId);
            return Optional.of(cachedUser);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
