package Ebrahem.Group.LMS.Service.Impl;

import Ebrahem.Group.LMS.Model.Dtos.CourseRequest;
import Ebrahem.Group.LMS.Model.Dtos.CourseResponse;
import Ebrahem.Group.LMS.Model.Entity.Course;
import Ebrahem.Group.LMS.Model.Entity.User;
import Ebrahem.Group.LMS.Repositories.CourseRepository;
import Ebrahem.Group.LMS.Repositories.UserRepository;
import Ebrahem.Group.LMS.Service.InstructorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

import static Ebrahem.Group.LMS.Model.Enums.Role.INSTRUCTOR;

@Service
@RequiredArgsConstructor
public class InstructorServiceImpl implements InstructorService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Override
    public CourseResponse CreateCourseResponse(CourseRequest course) {
        Course created = createCourse(course);
        return buildResponse(created);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Override
    public CourseResponse deleteCourse(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found by Id: " + courseId));

        courseRepository.delete(course);
        return buildResponse(course);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Override
    public CourseResponse updateCourse(UUID courseId, CourseRequest course) {
        Course courseById = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found by this ID: " + courseId));

        courseById.setCourseTitle(course.courseName());
        courseById.setCourseDuration(parseDuration(course.courseDuration()));

        Course updatedCourse = courseRepository.save(courseById);
        return buildResponse(updatedCourse);
    }

    private Course createCourse(CourseRequest course) {
        User instructor = userRepository.findByUserName(course.instructorName())
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));

        if (instructor.getRole() != INSTRUCTOR)
            throw new IllegalStateException("User is not an instructor");

        if (courseRepository.existsByCourseTitle(course.courseName()))
            throw new IllegalArgumentException("Course already exists: " + course.courseName());

        Duration duration = parseDuration(course.courseDuration());
        Course created = new Course(course.courseName(), duration, instructor);
        return courseRepository.save(created);
    }

    private String formatDuration(Duration duration) {
        if (duration == null) return "00h 00m";
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%02dh %02dm", hours, minutes);
    }

    private Duration parseDuration(String formatted) {
        if (formatted == null || formatted.isBlank()) return Duration.ZERO;
        formatted = formatted.trim().toLowerCase();

        long hours = 0;
        long minutes = 0;

        if (formatted.contains(":")) {
            String[] parts = formatted.split(":");
            hours = Long.parseLong(parts[0].trim());
            minutes = Long.parseLong(parts[1].trim());
        } else if (formatted.contains("h")) {
            String[] parts = formatted.split("h");
            hours = Long.parseLong(parts[0].trim());
            if (parts.length > 1) {
                String m = parts[1].replace("m", "").trim();
                if (!m.isEmpty()) minutes = Long.parseLong(m);
            }
        } else if (formatted.endsWith("m")) {
            minutes = Long.parseLong(formatted.replace("m", "").trim());
        }

        return Duration.ofHours(hours).plusMinutes(minutes);
    }

    private CourseResponse buildResponse(Course course) {
        return new CourseResponse(
                course.getInstructor().getUserId(),
                course.getCourseId(),
                course.getCourseTitle(),
                course.getInstructor().getUserName(),
                course.getCreatedAt(),
                course.getUpdatedAt(),
                formatDuration(course.getCourseDuration())
        );
    }
}
