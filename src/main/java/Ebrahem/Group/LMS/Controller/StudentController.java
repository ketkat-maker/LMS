package Ebrahem.Group.LMS.Controller;

import Ebrahem.Group.LMS.Model.Dtos.CourseFilter;
import Ebrahem.Group.LMS.Model.Dtos.CourseResponse;
import Ebrahem.Group.LMS.Service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/student")
public class StudentController {
    private final StudentService studentService;

//    @GetMapping(path = "/allCourses")
//    public ResponseEntity<List<CourseResponse>> allCoursesOnSystem() {
//
//        List<CourseResponse> allCourses = studentService.getAllCourses();
//        return new ResponseEntity<>(
//                allCourses,
//                HttpStatus.OK);
//    }

    @Operation(summary = "Search in courses")
    @GetMapping("/courses")
    public ResponseEntity<Page<CourseResponse>> getCourses(
            @ModelAttribute CourseFilter filter,
            Pageable pageable) {
        Page<CourseResponse> courses = studentService.getCourses(filter, pageable);
        return new ResponseEntity<>(
                courses,
                HttpStatus.OK);
    }
}
