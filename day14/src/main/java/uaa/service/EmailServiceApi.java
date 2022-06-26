package uaa.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "uaa.email-provider",name="name",havingValue = "api")
public class EmailServiceApi implements EmailService{

    private final SendGrid sendGrid;

    @Override
    public void send(String email, String msg) {
        val from = new Email("service@lxh.com");
        val to = new Email(email);
        val subject = "SpringSecurity登录验证码";
        val content = new Content("text/plain", "验证码为:" + msg);
        val mail = new Mail(from, subject, to, content);
        val request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            val response = sendGrid.api(request);
            if (response.getStatusCode() == 200) {
                log.info("邮件发送成功");
            } else {
                log.error(response.getBody());
            }
        }catch (IOException e){
            log.error("请求发生异常{}",e.getLocalizedMessage());
        }


    }
}
