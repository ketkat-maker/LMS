package Ebrahem.Group.LMS.Service;

import Ebrahem.Group.LMS.Model.Dtos.EmailServiceDto;

public interface MailService {
    void sendEmail(EmailServiceDto emailRequest);

}
