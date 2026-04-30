package Ebrahem.Group.LMS.Repositories;

import Ebrahem.Group.LMS.Model.Entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CourseRepository extends JpaRepository<Course, String>, JpaSpecificationExecutor<Course> {

    boolean existsByCourseTitle(String courseName);

    Optional<Course> findByCourseTitle(String courseName);
}
