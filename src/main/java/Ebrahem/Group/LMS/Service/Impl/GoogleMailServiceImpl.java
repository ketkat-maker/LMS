package Ebrahem.Group.LMS.Service.Impl;

import Ebrahem.Group.LMS.Model.Dtos.EmailServiceDto;
import Ebrahem.Group.LMS.Service.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class GoogleMailServiceImpl implements MailService {
    private final JavaMailSender mailSender;
    private final String baseMail;

    public GoogleMailServiceImpl(
            @Value("${spring.mail.username}") String baseMail,
            JavaMailSender mailSender) {
        this.baseMail = baseMail;
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(EmailServiceDto emailRequest) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailRequest.to());
        message.setFrom(baseMail);
        message.setSubject(emailRequest.subject());
        message.setText(emailRequest.body());
        mailSender.send(message);
    }


}
