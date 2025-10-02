package Ebrahem.Group.LMS.Mapper;
import Ebrahem.Group.LMS.Model.Dtos.Student;
import Ebrahem.Group.LMS.Model.Entity.User;

import java.util.List;

public interface StudentMapper {
    List<Student> toStudentFromEntity(List<User> student);

}
