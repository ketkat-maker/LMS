package Ebrahem.Group.LMS.Service.Impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtProviderServiceImplTest {

    UserDetails user = new User("Ebrahem hany", "Ebrahem-hany", new ArrayList<>());
    @InjectMocks
    private JwtProviderServiceImpl jwtProviderService; // this is what we will test above dependencies in tested class

    @BeforeEach
    void SetUp() {
        jwtProviderService = new JwtProviderServiceImpl(
                null,
                "VGhpcy1pcy1hLXNlY3JldC1rZXktZm9yLXRlc3Rpbmc="
        );
    }

    @Test
    void shouldGenerateTokenAndValuedName() {
        //Given
        String testGenerateToken = jwtProviderService.generateToken(user);
        //when
        String extractedUserName = jwtProviderService.extractUsername(testGenerateToken);
        //then
        assertEquals("Ebrahem hany", extractedUserName);
    }

    @Test
    void tokenIsValued() {
        //given
        String valuedToken = jwtProviderService.generateToken(user);
        //when
        boolean tokenValid = jwtProviderService.isTokenValid(valuedToken, user);
        //then
        assertTrue(tokenValid);
    }

    @Test
    void tokenNotValued() {
        UserDetails user1 = new User("Ebrahem-hany", "Ebrahem-hany", new ArrayList<>());
        String valuedToken = jwtProviderService.generateToken(user);
        //when
        boolean tokenValid = jwtProviderService.isTokenValid(valuedToken, user1);
        //then
        assertFalse(tokenValid);
    }
}