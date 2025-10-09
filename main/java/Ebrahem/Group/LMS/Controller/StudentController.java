package Ebrahem.Group.LMS.Controller;

import Ebrahem.Group.LMS.Model.Dtos.CourseResponse;
import Ebrahem.Group.LMS.Service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/student")
public class StudentController {
private final StudentService studentService;
    @GetMapping(path = "/allCourses")
    public ResponseEntity<List<CourseResponse>>allCoursesOnSystem(){

        List<CourseResponse> allCourses = studentService.getAllCourses();
        return new ResponseEntity<>(
                allCourses,
                HttpStatus.ACCEPTED);

    }

}
