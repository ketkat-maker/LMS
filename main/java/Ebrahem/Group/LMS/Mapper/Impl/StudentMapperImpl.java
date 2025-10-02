package Ebrahem.Group.LMS.Mapper.Impl;

import Ebrahem.Group.LMS.Mapper.StudentMapper;
import Ebrahem.Group.LMS.Model.Dtos.Student;
import Ebrahem.Group.LMS.Model.Entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class StudentMapperImpl implements StudentMapper {

    @Override
    public List<Student> toStudentFromEntity(List<User> users) {
        return users.stream()
                .map(user -> new Student(
                        user.getUserId(),
                        user.getUserName(),
                        user.getUserEmail(),
                        user.getCreatedAt(),
                        user.getRole()
                ))
                .toList();
    }

}
