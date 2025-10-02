package Ebrahem.Group.LMS.Repositories;

import Ebrahem.Group.LMS.Model.Entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

    boolean existsByCourseTitle(String courseName);
}
