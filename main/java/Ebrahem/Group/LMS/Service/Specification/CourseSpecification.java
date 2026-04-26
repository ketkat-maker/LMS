package Ebrahem.Group.LMS.Service.Specification;

import Ebrahem.Group.LMS.Model.Dtos.CourseFilter;
import Ebrahem.Group.LMS.Model.Entity.Course;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseSpecification {
    public static Specification<Course> withFilter(CourseFilter filter) {
        return (root, query, cd) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.courseTitle() != null) {
                predicates.add(
                        cd.like(
                                cd.lower(root.get("courseTitle")),
                                "%" + filter.courseTitle().toLowerCase() + "%"));
            }

            if (filter.createdFrom() != null) {
                predicates.add(
                        cd.greaterThanOrEqualTo(
                                root.get("createdAt"), filter.createdFrom()));
            }
            if (filter.createdTo() != null) {
                predicates.add(
                        cd.lessThanOrEqualTo(root.get("createdAt"), filter.createdTo()));
            }

            if (filter.updatedFrom() != null) {
                predicates.add(
                        cd.greaterThanOrEqualTo(root.get("updatedAt"), filter.updatedFrom()));
            }
            if (filter.updatedTo() != null) {
                predicates.add(
                        cd.lessThanOrEqualTo(root.get("updatedAt"), filter.updatedTo())
                );
            }
            return cd.and(predicates.toArray(Predicate[]::new));
        };
    }
}
