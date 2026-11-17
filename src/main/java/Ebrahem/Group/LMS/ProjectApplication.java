package Ebrahem.Group.LMS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;

@SpringBootApplication
public class ProjectApplication {
    private static final Logger log =
            LoggerFactory.getLogger(ProjectApplication.class);

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        ApplicationContext run = SpringApplication.run(ProjectApplication.class, args
        );
//        JwtProviderService jwtService = run.getBean(JwtProviderService.class);
//
//        // 2. تجهيز User وهمي للتجربة
//        User dummyUser = new User(
//                "Ebrahem Hany",
//                "ebrahemhany15@gmail.com",
//                "1Ebrahem45",
//                Role.STUDENT,
//                OtpStatus.COMPLETE,
//                LocalDateTime.now()
//        );
//        UserSecurity userDetails = new UserSecurity(dummyUser);
//
//        // 3. توليد التوكن واستخراج البيانات منه
//        String token = jwtService.generateToken(userDetails);
//        String extractedId = jwtService.extractTokenId(token);
//        String extractedEmail = jwtService.extractUsername(token);
//        boolean isValid = jwtService.isTokenValid("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlYnJhaGVtaGFueTEzQGdtYWlsLmNvbSIsImp0aSI6Ims5OXRvODNzejFyaCIsImlhdCI6MTc5NDk1NTI2MCwiZXhwIjoxNzk1MDQxNjYwfQ.22l7EO7LpW4eI486ni8vZIjcQbdWuUrLpZOGzI8EDMw", userDetails);
//
//        // 4. طباعة النتيجة في الـ Console
//        log.info("=== JWT TEST RESULTS ===");
//        log.info("Generated Token : {}", token);
//        log.info("Extracted ID    : {}", extractedId); // لازم يطبع كود من 12 حرف وميكونش null
//        log.info("Extracted Email : {}", extractedEmail);
//        log.info("Is Token Valid? : {}", isValid);
//        Object googleMailServiceImpl = run.getBean(GoogleMailServiceImpl.class);
//        googleMailServiceImpl.getClass()
//                .getMethod("sendEmail", EmailRequest.class)
//                .invoke(googleMailServiceImpl, new EmailRequest(
//                        "ebrahemhany13@gmail.com",
//                        "First send email from Spring Boot",
//                        "hi katkat"
//                ));
//        Object otpServiceImpl = run.getBean(OtpServiceImpl.class);
//        String generatedOtp = (String) otpServiceImpl.getClass()
//                .getMethod("generateOtp", String.class)
//                .invoke(otpServiceImpl, "ebrahemhany13@gmail.com");
//
//        Object valedOtp = otpServiceImpl.getClass().
//                getMethod("validateOtp", String.class, String.class).
//                invoke(otpServiceImpl, "test@example.com", generateOtp);
//
//        Object valedOtp2 = otpServiceImpl.getClass().
//                getMethod("validateOtp", String.class, String.class).
//                invoke(otpServiceImpl, "test@example.com", generateOtp);

//        log.info("Generated OTP: {}", generateOtp);
//        log.info("Validated OTP: {}", valedOtp);
//        log.info("Validated OTP 2: {}", valedOtp2);
//        AuthServiceImpl authService = run.getBean(AuthServiceImpl.class);
//        String generatedOtp = (String) authService.getClass().getMethod("LogIn", LogInRequest.class).
//                invoke(authService, new LogInRequest("ebrahemhany13@gmail.com", "1Ebrahem45"));
//
//        authService.getClass().getMethod("verifyLoginOtp", String.class, String.class)
//                .invoke(authService, "ebrahemhany13@gmail.com", generatedOtp);
//        authService.getClass().getMethod("verifyLoginOtp", String.class, String.class)
//                .invoke(authService, "ebrahemhany13@gmail.com", generatedOtp);
    }

}


