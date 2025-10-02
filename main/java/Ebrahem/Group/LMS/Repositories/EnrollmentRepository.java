package Ebrahem.Group.LMS.Repositories;

import Ebrahem.Group.LMS.Model.Entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
}
