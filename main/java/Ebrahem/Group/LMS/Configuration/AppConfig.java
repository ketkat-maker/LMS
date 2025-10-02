    package Ebrahem.Group.LMS.Configuration;

    import Ebrahem.Group.LMS.Repositories.UserRepository;
    import Ebrahem.Group.LMS.Security.LMSUserDetailsService;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
    import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.crypto.factory.PasswordEncoderFactories;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

    import static org.springframework.http.HttpMethod.GET;
    import static org.springframework.http.HttpMethod.POST;
    @EnableWebSecurity
    @EnableMethodSecurity
    @Configuration
    public class AppConfig {
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new LMSUserDetailsService(userRepository);
    }
        @Bean
        public SecurityFilterChain securityFilterChain(
                HttpSecurity http,
                JwtAuthenticationFilter jwtAuthenticationFilter
        ) throws Exception {
            http
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(
                                    "/v3/api-docs/**",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html").permitAll()
                            .requestMatchers(POST, "/api/v1/auth/**").permitAll()
                            .requestMatchers(GET, "/api/v1/students").hasRole("INSTRUCTOR")
                            .requestMatchers(POST,"/api/v1/course").hasAnyRole("INSTRUCTOR")
                            .anyRequest().authenticated()
                    )
                    .csrf(AbstractHttpConfigurer::disable)
                    .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }


        @Bean
            public PasswordEncoder coder() {
                return PasswordEncoderFactories.createDelegatingPasswordEncoder();
            }

            @Bean
            public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
                return configuration.getAuthenticationManager();
            }
        }

