package Ebrahem.Group.LMS.Mapper.Impl;

import Ebrahem.Group.LMS.Mapper.LogMapper;
import Ebrahem.Group.LMS.Model.Dtos.SignUpRequest;
import Ebrahem.Group.LMS.Model.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogMapperImpl implements LogMapper {
    private final PasswordEncoder passwordEncoder;
    @Override
    public User toEntityFromSignUp(SignUpRequest signUpRequest) {
        return new User(
                passwordEncoder.encode(signUpRequest.password()),
                signUpRequest.role()
                ,signUpRequest.userEmail()
                ,signUpRequest.firstName()+" "+signUpRequest.lastName());
        }

}
