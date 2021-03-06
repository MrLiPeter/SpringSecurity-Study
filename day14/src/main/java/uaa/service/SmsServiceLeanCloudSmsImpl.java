package uaa.service;

import lombok.val;


import cn.leancloud.sms.AVSMS;
import cn.leancloud.sms.AVSMSOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "uaa.email-provider",name="name",havingValue = "leanCloud")
public class SmsServiceLeanCloudSmsImpl implements SmsService{

    @Override
    public void send(String mobile, String msg) {
        val option = new AVSMSOption();
        option.setTtl(10);
        option.setApplicationName("慕课网实战Spring Security");
        option.setOperation("两步验证");
        option.setTemplateName("登录验证");
        option.setSignatureName("慕课网");
        option.setType(AVSMS.TYPE.TEXT_SMS);
        option.setEnvMap(Map.of("smsCode", msg));
        AVSMS.requestSMSCodeInBackground(mobile, option)
                .take(1)
                .subscribe(
                        (res) -> log.info("短信发送成功 {}", res),
                        (err) -> log.error("发送短信时产生服务端异常 {}", err.getLocalizedMessage())
                );
    }


}
