package Ebrahem.Group.LMS.Configuration;


import Ebrahem.Group.LMS.Security.LMSUserDetailsService;
import Ebrahem.Group.LMS.Security.LMSUserSecurity;
import Ebrahem.Group.LMS.Service.AuthenticateService;
import Ebrahem.Group.LMS.Service.Impl.AuthenticateServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticateServiceImpl authenticateService;
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        try {
//            String token = extractToken(request);
//            if (token != null) {
//                UserDetails userDetails = authenticateService.validToken(token);
//
//
//                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//                        userDetails,
//                        null,
//                        userDetails.getAuthorities()
//                );
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//
//                if (userDetails instanceof LMSUserSecurity) {
//                    request.setAttribute("userId", ((LMSUserSecurity) userDetails).getUserId());
//                }
//            }
//        } catch (Exception e){
//            logger.warn("Received invalid token auth");
//        }
//        filterChain.doFilter(request,response);
//    }
//
//    private String extractToken(HttpServletRequest request)
//    {
//        String authentication = request.getHeader("Authentication");
//
//        if (authentication!=null&&authentication.startsWith("Bearer ")){
//            return authentication.substring(7);
//        }
//        return null;
//    }
//    private final HandlerExceptionResolver HandlerExceptionResolver;
    private final LMSUserDetailsService userDetailsService;
@Override
protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
) throws ServletException, IOException {
    final String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
    }

    try {
        final String jwt = authHeader.substring(7);
        final String userEmail = authenticateService.extractUsername(jwt);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (userEmail != null && authentication == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            if (authenticateService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    } catch (Exception exception) {
//        HandlerExceptionResolver.resolveException(request, response, null, exception);
    }
}


}