package Ebrahem.Group.LMS.Repositories;

import Ebrahem.Group.LMS.Model.Entity.Course;
import Ebrahem.Group.LMS.Model.Entity.Enrollment;
import Ebrahem.Group.LMS.Model.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    boolean existsByUserAndCourse(User userId, Course courseId);
}
