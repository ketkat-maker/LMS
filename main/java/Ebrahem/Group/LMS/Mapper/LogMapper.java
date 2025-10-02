package Ebrahem.Group.LMS.Mapper;

import Ebrahem.Group.LMS.Model.Dtos.SignUpRequest;
import Ebrahem.Group.LMS.Model.Entity.User;

public interface LogMapper {
//     User toEntityFromLogIn(LogInRequest request);
    User toEntityFromSignUp(SignUpRequest signUpRequest);
}
