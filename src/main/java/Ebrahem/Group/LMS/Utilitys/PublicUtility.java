package Ebrahem.Group.LMS.Utilitys;

import Ebrahem.Group.LMS.Excption.UnauthorizedActionException;
import Ebrahem.Group.LMS.Model.Entity.User;
import Ebrahem.Group.LMS.Security.UserSecurity;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class PublicUtility {
    private static final char[] ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final int ID_LENGTH = 12;


    public static String generateId() {
        return NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, ALPHABET, ID_LENGTH);
    }

    public static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserSecurity(User user))) {
            throw new UnauthorizedActionException("User is not authenticated");
        }
        return user;
    }
}