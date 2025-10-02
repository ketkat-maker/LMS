package Ebrahem.Group.LMS.Configuration;


import Ebrahem.Group.LMS.Security.LMSUserSecurity;
import Ebrahem.Group.LMS.Service.AuthenticateService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Configuration
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticateService authenticateService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);
            if (token != null) {
                UserDetails userDetails = authenticateService.validToken(token);


                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                if (userDetails instanceof LMSUserSecurity) {
                    request.setAttribute("userId", ((LMSUserSecurity) userDetails).getUserId());
                }
            }
        } catch (Exception e){
            logger.warn("Received invalid token auth");
        }
        filterChain.doFilter(request,response);
    }

    private String extractToken(HttpServletRequest request)
    {
        String authentication = request.getHeader("Authentication");

        if (authentication!=null&&authentication.startsWith("Bearer ")){
            return authentication.substring(7);
        }
        return null;
    }


}