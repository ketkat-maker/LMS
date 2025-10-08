package Ebrahem.Group.LMS;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;

@SpringBootTest
class ProjectApplicationTests {

	@Test
	void contextLoads() {
        SecretKey key = Jwts.SIG.HS256.key().build();
        String base64Key = Encoders.BASE64.encode(key.getEncoded());
        System.out.println("Generated key: " + base64Key);
	}

}
