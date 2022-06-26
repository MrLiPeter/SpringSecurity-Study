package uaa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "uaa.email-provider",name="name",havingValue = "smtp")
public class EmailServiceSmtp implements EmailService{

    private final JavaMailSender javaMailSender;

    @Override
    public void send(String email, String msg) {
        val message = new SimpleMailMessage();
        message.setFrom("service@lxh.com");
        message.setTo(email);
        message.setSubject("SpringSecurity登录验证码");
        message.setText("验证码为:" + msg);
        javaMailSender.send(message);
    }
}
