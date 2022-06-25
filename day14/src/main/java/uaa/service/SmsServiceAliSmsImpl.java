package uaa.service;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.aliyuncs.exceptions.ServerException;


@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "uaa.sms-provider",name="name",havingValue = "ali")
public class SmsServiceAliSmsImpl implements SmsService{
    private final IAcsClient client;

    public void send(String mobile,String msg){
        val request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysAction("SendSms");
        request.setSysVersion("2022-06-25");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", "登录验证");
        request.putQueryParameter("TemplateCode", "SMS_1610048");
        request.putQueryParameter("TemplateParam", "{\"code\":\"" +
                msg +
                "\",\"product\":\"实战Spring Security\"}");
        try {
            val response = client.getCommonResponse(request);
            log.info("短信发送结果 {}", response.getData());
        } catch (ServerException e) {
            log.error("发送短信时产生服务端异常 {}", e.getLocalizedMessage());
        } catch (ClientException e) {
            log.error("发送短信时产生客户端异常 {}", e.getLocalizedMessage());
        }
    }
}
